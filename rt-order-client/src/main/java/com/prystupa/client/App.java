package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        final HazelcastInstance client = HazelcastClient.newHazelcastClient();
        final EventIngester ingester = new EventIngester(client);

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            if (line.equals("clear")) {
                ingester.clear();
                continue;
            }

            String[] order = line.split("\\s");
            String id = order[0];
            String parentId = order[1];
            String primeId = order.length > 2 ? order[2] : "PrimeID";
            Event event = new Event(id, parentId, primeId);

            ingester.ingest(event);
        }

        client.shutdown();
    }
}

