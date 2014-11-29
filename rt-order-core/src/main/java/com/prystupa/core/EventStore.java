package com.prystupa.core;

import com.hazelcast.core.*;
import com.prystupa.core.command.MultiMapKeyCountCommand;
import com.prystupa.core.monitor.LoaderStats;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class EventStore {

    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;
    private final IExecutorService executionService;
    private final ISet<LoaderStats> loaders;

    public EventStore(final HazelcastInstance hazelcast) {
        parents = hazelcast.getMap("parents");
        chains = hazelcast.getMultiMap("chains");
        executionService = hazelcast.getExecutorService("default");
        loaders = hazelcast.<LoaderStats>getSet("loaderStats");
    }

    public void clear() {
        chains.clear();
        parents.clear();

    }

    public Collection<Event> chain(EventID eventID) {
        return chains.get(eventID);
    }

    public int chainCount() throws ExecutionException, InterruptedException {
        final MultiMapKeyCountCommand.ResultCollector collector = new MultiMapKeyCountCommand.ResultCollector();
        executionService.submitToAllMembers(new MultiMapKeyCountCommand("chains"), collector);
        return collector.getResult().get();
    }

    public int eventCount() {
        return chains.size();
    }

    public void clearLoaders() {
        loaders.clear();
    }
}
