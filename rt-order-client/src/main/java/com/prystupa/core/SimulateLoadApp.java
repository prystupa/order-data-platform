package com.prystupa.core;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientAwsConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SimulateLoadApp {

    private static Logger logger = LoggerFactory.getLogger(SimulateLoadApp.class);
    private static Random rand = new Random();

    public static void main(String[] args) throws IOException {

        final ClientConfig config = new XmlClientConfigBuilder("hazelcast-client.xml").build();
        final String accessKey = System.getProperty("aws.access-key");
        if (accessKey != null) {
            final ClientAwsConfig awsConfig = config.getNetworkConfig().getAwsConfig();
            awsConfig.setAccessKey(accessKey);
            awsConfig.setSecretKey(System.getProperty("aws.secret-key"));
        }
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final EventIngester ingester = new EventIngester(client);

        final List<Event> events = generateEvents(100000);
        Collections.shuffle(events);

        int batch = 0;
        for (Event event : events) {
            ingester.ingest(event);
            batch++;
            if (batch % 1000 == 0) {
                logger.info("Ingested {} events", batch);
            }
        }

        client.shutdown();
    }

    private static List<Event> generateEvents(final int count) {
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

    private static int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }
}
