package com.prystupa;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Scanner;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(App.class);

        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        MultiMap<String, String> chains = client.getMultiMap("chains");
        chains.clear();
        IMap<String, String> chainLookup = client.getMap("chainLookup");
        chainLookup.clear();

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            String[] order = line.split("\\s");
            String id = order[0];
            String parentId = order[1];

            String parentChainId = chainLookup.get(parentId);
            if (parentChainId == null) {
                parentChainId = UUID.randomUUID().toString();
                chains.put(parentChainId, parentId);
                chainLookup.put(parentId, parentChainId);
                logger.info("Create new chain {} and added {} order to it", parentChainId, parentId);
            }

            String childChainId = chainLookup.get(id);
            if (childChainId == null) {
                chains.put(parentChainId, id);
                chainLookup.put(id, parentChainId);
                logger.info("Added child order {} to chain {}", id, parentChainId);
            } else if (!childChainId.equals(parentChainId)) {
                // merge
                Collection<String> orders = chains.get(childChainId);
                for (String orderId : orders) {
                    chains.put(parentChainId, orderId);
                    chainLookup.put(orderId, parentChainId);
                    logger.info("Merged order {} from chain {} to chain {}", orderId, childChainId, parentChainId);
                }

                chains.remove(childChainId);
            }
        }
    }
}

