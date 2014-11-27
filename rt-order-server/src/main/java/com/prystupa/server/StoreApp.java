package com.prystupa.server;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;

public class StoreApp {

    public static void main(String[] args) {

        final HazelcastInstance server = Hazelcast.newHazelcastInstance();

        // Setup chain multi map listener
        final EventChainListener eventChainListener = new EventChainListener();
        eventChainListener.setHazelcastInstance(server);
        final MultiMap<EventID, Event> chains = server.getMultiMap("chains");
        chains.addLocalEntryListener(eventChainListener);

        // Setup parent/child multi map listener
        final ParentEventListener parentEventListener = new ParentEventListener();
        parentEventListener.setHazelcastInstance(server);
        final IMap<EventID, String> parents = server.getMap("parents");
        parents.addLocalEntryListener(parentEventListener);
    }
}
