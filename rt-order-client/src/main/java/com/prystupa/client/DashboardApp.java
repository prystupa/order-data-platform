package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventIngester;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DashboardApp {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        final String DEFAULT_PRIME_ID = "PrimeID";
        final ClientConfig config = ClientUtils.buildConfig();
        final HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        final EventIngester ingester = new EventIngester(client);

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            if (line.equals("clear")) {
                ingester.clear();
                continue;
            }
            if (line.equals("count")) {
                System.out.println(ingester.chainCount());
                continue;
            }
            if (line.startsWith("chain")) {
                String[] parts = line.split("\\s");
                String chainId = parts[1];
                String primeId = parts.length > 2 ? parts[2] : DEFAULT_PRIME_ID;
                System.out.println(ingester.chain(new EventID(chainId, primeId)));
                continue;
            }

            String[] order = line.split("\\s");
            String id = order[0];
            String parentId = order[1];
            String primeId = order.length > 2 ? order[2] : DEFAULT_PRIME_ID;
            Event event = new Event(id, parentId, primeId);

            ingester.ingest(event);
        }

        client.shutdown();
    }
}

