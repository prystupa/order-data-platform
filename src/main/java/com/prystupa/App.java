package com.prystupa;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.*;

public class App {

    public static void main(String[] args) {
//        final Logger logger = LoggerFactory.getLogger(App.class);

        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

//        MultiMap<String, String> chains = client.getMultiMap("chains");
//        chains.clear();
        final IMap<ChainEventKey, ChainEvent> chains = client.getMap("chains");
        final IMap<ChainEventKey, String> chainLookup = client.getMap("chainLookup");

        chains.addInterceptor(new ChainLinkerInterceptor());

        System.out.println("Enter order pairs, empty string to exit:");
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!(line = scanner.nextLine()).equals("")) {
            if(line.equals("clear")) {
                chains.clear();
                chainLookup.clear();
                continue;
            }

            String[] order = line.split("\\s");
            ChainEvent event = new ChainEvent(order[0], order[1], "primeID");

            chains.put(event.key, event);
//            String parentChainId = chainLookup.putIfAbsent(parentId, parentId);
//            if (parentChainId == null) {
//                parentChainId = parentId;
//                chains.put(parentChainId, parentId);
//                logger.info("Create new chain '{}' and added order '{}' to it", parentChainId, parentId);
//            }
//
//            String childChainId = chainLookup.get(id);
//            if (childChainId == null) {
//                chains.put(parentChainId, id);
//                chainLookup.put(id, parentChainId);
//                logger.info("Added child order '{}' to chain '{}'", id, parentChainId);
//            } else if (!childChainId.equals(parentChainId)) {
//                merge
//                Collection<String> orders = chains.get(childChainId);
//                for (String orderId : orders) {
//                    chains.put(parentChainId, orderId);
//                    chainLookup.put(orderId, parentChainId);
//
//                    chains.remove(childChainId);
//
//                    logger.info("Merged order '{}' from chain '{}' to chain '{}'", orderId, childChainId, parentChainId);
//                }
//
//                chains.remove(childChainId);
//            }
        }

        client.shutdown();
    }
}

