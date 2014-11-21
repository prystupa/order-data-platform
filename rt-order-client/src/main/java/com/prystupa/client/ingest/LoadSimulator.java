package com.prystupa.client.ingest;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientAwsConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;
import com.prystupa.core.EventIngester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LoadSimulator extends UntypedActor {
    private Logger logger = LoggerFactory.getLogger(LoadSimulator.class);
    private Random rand = new Random();
    private final int total = 100000;
    private int processed = 0;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        final ClientConfig config = new XmlClientConfigBuilder("hazelcast-client.xml").build();
        final String accessKey = System.getProperty("aws.access-key");
        if (accessKey != null) {
            final ClientAwsConfig awsConfig = config.getNetworkConfig().getAwsConfig();
            awsConfig.setAccessKey(accessKey);
            awsConfig.setSecretKey(System.getProperty("aws.secret-key"));
        }
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final EventIngester ingester = new EventIngester(client);

        final List<Event> events = generateEvents(total);
        Collections.shuffle(events);

        for (Event event : events) {
            final ActorRef eventIngester = getContext().actorOf(Props.create(Ingester.class, ingester));
            getContext().watch(eventIngester);
            eventIngester.tell(event, self());
        }

        client.shutdown();
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof Terminated) {
            processed++;
            if (processed % 1000 == 0) {
                logger.info("Ingested {} events", processed);
            }

            if (processed == total) {
                getContext().system().shutdown();
            }
        } else {
            unhandled(message);
        }
    }

    private List<Event> generateEvents(final int count) {
        List<Event> list = new ArrayList<>(count);

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

        logger.info("Created {} different chains", chains);
        return list;
    }

    private int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }
}
