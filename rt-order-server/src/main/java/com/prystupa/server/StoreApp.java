package com.prystupa.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.RoundRobinPool;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import com.prystupa.server.worker.LinkerWorker;

public class StoreApp {

    public static void main(String[] args) {

        final HazelcastInstance server = Hazelcast.newHazelcastInstance();
        final EventStore store = new EventStore(server);

        // Akka setup
        final ActorSystem system = ActorSystem.create();
        final ActorRef linker = system.actorOf(LinkerWorker.props(store).withRouter(new RoundRobinPool(5000)));

        // Setup chain multi map listener
        final EventChainListener eventChainListener = new EventChainListener(linker);
        final MultiMap<EventID, Event> chains = server.getMultiMap("chains");
        chains.addLocalEntryListener(eventChainListener);

        // Setup parent/child multi map listener
        final ParentEventListener parentEventListener = new ParentEventListener(linker);
        final IMap<EventID, String> parents = server.getMap("parents");
        parents.addLocalEntryListener(parentEventListener);
    }
}
