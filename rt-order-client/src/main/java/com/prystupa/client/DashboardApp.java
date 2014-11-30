package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import com.prystupa.core.command.LoaderStatsCommand;
import com.prystupa.core.command.StoreCommand;
import com.prystupa.core.monitor.LoaderStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DashboardApp {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        final Logger logger = LoggerFactory.getLogger(DashboardApp.class);
        final String DEFAULT_PRIME_ID = "PrimeID";
        final ClientConfig config = ClientUtils.buildConfig();
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final EventStore store = new EventStore(client);
        final IExecutorService executorService = client.getExecutorService("default");

        final ICountDownLatch latch = client.getCountDownLatch("go");
        latch.trySetCount(1);
        System.out.println("GO latch count: " + latch.getCount());

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            if (line.equals("clear")) {
                store.clear();
            } else if (line.equals("clear loaders")) {
                store.clearLoaders();
            } else if (line.equals("count")) {
                System.out.printf("%,d/%,d\n", store.chainCount(), store.eventCount());
            } else if (line.startsWith("chain")) {
                String[] parts = line.split("\\s");
                String chainId = parts[1];
                String primeId = parts.length > 2 ? parts[2] : DEFAULT_PRIME_ID;
                System.out.println(store.chain(new EventID(chainId, primeId)));
            } else if (line.startsWith("loaders")) {
                final String[] largs = line.split("\\s");
                final int target = largs.length > 1 ? Integer.parseInt(largs[1]) : -1;
                do {
                    final LoaderStatsCommand.ResultCollector collector = new LoaderStatsCommand.ResultCollector();
                    executorService.submitToAllMembers(new LoaderStatsCommand(), collector);
                    final Set<LoaderStats> loaders = collector.getResult().get();
                    final int chains = loaders.stream().mapToInt(LoaderStats::getChains).sum();
                    final int events = loaders.stream().mapToInt(LoaderStats::getEvents).sum();
                    System.out.printf("Loaders: %,d, chains: %,d, events: %,d\n", loaders.size(), chains, events);
                    if (target > loaders.size()) {
                        Thread.sleep(5000);
                    } else {
                        break;
                    }
                } while (true);
            } else if (line.equals("go")) {
                final LoaderStatsCommand.ResultCollector collector = new LoaderStatsCommand.ResultCollector();
                executorService.submitToAllMembers(new LoaderStatsCommand(), collector);
                final Set<LoaderStats> loaders = collector.getResult().get();
                final int chains = loaders.stream().mapToInt(LoaderStats::getChains).sum();
                final int events = loaders.stream().mapToInt(LoaderStats::getEvents).sum();

                latch.countDown();
                logger.info("GO latch count: {}, expected chains: {}, events: {}", latch.getCount(), chains, events);

                int count;
                do {
                    count = store.chainCount();
                    if (count >= chains) {
                        logger.info("Linked chains count: {}", count);
                        Thread.sleep(5 * 1000);
                    }
                } while (count != chains);

                latch.trySetCount(1);
                System.out.println("GO latch count: " + latch.getCount());
            } else {
                String[] order = line.split("\\s");
                String id = order[0];
                String parentId = order[1];
                String primeId = order.length > 2 ? order[2] : DEFAULT_PRIME_ID;
                Event event = new Event(id, parentId, primeId);

                executorService.submitToKeyOwner(new StoreCommand(event), primeId, new ExecutionCallback() {
                    @Override
                    public void onResponse(Object response) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        }

        client.shutdown();
    }
}

