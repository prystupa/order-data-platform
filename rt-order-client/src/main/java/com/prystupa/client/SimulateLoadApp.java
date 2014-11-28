package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.prystupa.core.Event;
import com.prystupa.core.command.StoreCommand;
import com.prystupa.core.monitor.LoaderStats;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulateLoadApp {

    private static Logger logger = LoggerFactory.getLogger(SimulateLoadApp.class);
    private static Random rand = new Random();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ParseException {

        final Options options = new Options();
        options.addOption("n", true, "number of order events to emulate");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);

        int total = Integer.parseInt(cmd.getOptionValue("n", "1"));

        final ClientConfig config = ClientUtils.buildConfig();
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final IExecutorService executorService = client.getExecutorService("default");

        final List<Event> events = new ArrayList<>(total);
        final int chains = generateEvents(total, events);
        Collections.shuffle(events);
        client.<LoaderStats>getSet("loaderStats").add(new LoaderStats(client.getLocalEndpoint().getUuid(), chains, total));
        logger.info("Created {} chain(s)", chains);

        final ICountDownLatch latch = client.getCountDownLatch("go");
        latch.await(1, TimeUnit.DAYS);

        final AtomicInteger batch = new AtomicInteger(0);
        for (Event event : events) {
            final CompletableFuture<Object> future = ingest(executorService, event);
            future.thenRun(() -> {
                int ingested = batch.addAndGet(1);
                if (ingested % 10000 == 0 || ingested == total) {
                    logger.info("Ingested {} event(s)", ingested);
                }
            });
        }

        for (; ; ) {
            if (batch.get() >= total) {
                break;
            }
        }

        client.shutdown();
    }

    /**
     * Generates requested number of events grouping them randomly into chains
     *
     * @param count requested number of events to generate
     * @param list  list to add generated events to
     * @return the number of generated chains
     */
    private static int generateEvents(final int count, final List<Event> list) {

        int remaining = count;
        int chains = 0;
        while (remaining > 0) {
            int chain = Math.min(randInt(1, 20), remaining);
            remaining -= chain;

            String parent = UUID.randomUUID().toString();
            String primeId = "PrimeID-" + randInt(1, 5000);
            list.add(new Event(parent, parent, primeId));
            --chain;
            while (--chain >= 0) {
                String child = UUID.randomUUID().toString();
                list.add(new Event(child, parent, primeId));
                parent = child;
            }

            chains++;
        }

        return chains;
    }

    private static int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    public static CompletableFuture<Object> ingest(final IExecutorService executorService, final Event event) throws InterruptedException {

        final CompletableFuture<Object> result = new CompletableFuture<>();
        executorService.submitToKeyOwner(new StoreCommand(event), event.getPrimeId(), new ExecutionCallback<Object>() {
            @Override
            public void onResponse(Object response) {
                logger.debug("Submitted event {}", event);
                result.complete(response);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Failed submitting event", t);
                result.completeExceptionally(t);
            }
        });

        return result;
    }
}
