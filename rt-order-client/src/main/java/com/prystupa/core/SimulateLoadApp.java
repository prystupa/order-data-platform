package com.prystupa.core;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimulateLoadApp {

    private static Logger logger = LoggerFactory.getLogger(SimulateLoadApp.class);
    private static Random rand = new Random();

    public static void main(String[] args) {

        final HazelcastInstance client = HazelcastClient.newHazelcastClient();
        final EventIngester ingester = new EventIngester(client);

        List<Event> events = generateEvents(100000);
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
        }

        return list;
    }

    private static int randInt(int min, int max) {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }
}
