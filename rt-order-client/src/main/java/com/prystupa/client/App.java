package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);

        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        IMap<EventID, String> parents = client.getMap("parents");
        MultiMap<EventID, String> chains = client.getMultiMap("chains");

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            if (line.equals("clear")) {
                chains.clear();
                continue;
            }

            String[] order = line.split("\\s");
            String id = order[0];
            String parentId = order[1];
            String primeId = order.length > 2 ? order[2] : "PrimeID";
            EventID eventID = new EventID(id, primeId);

            parents.put(eventID, parentId);
            chains.put(eventID, id);
            logger.info("saved '{}' to chain '{}'", id, eventID);
        }

        client.shutdown();
    }
}

