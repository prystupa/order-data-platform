package com.prystupa.core;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.command.MultiMapKeyCountCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class EventStore {

    private final Logger logger = LoggerFactory.getLogger(EventStore.class);
    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;
    private final IExecutorService executionService;


    public EventStore(final HazelcastInstance hazelcast) {
        parents = hazelcast.getMap("parents");
        chains = hazelcast.getMultiMap("chains");
        executionService = hazelcast.getExecutorService("default");
    }

    public void save(final Event event) {
        final EventID eventID = new EventID(event.getEventId(), event.getPrimeId());
        parents.put(eventID, event.getParentId());

        final EventID rootID = findRoot(eventID);
        chains.put(rootID, event);
        logger.debug("saved event '{}' to chain '{}'", event, rootID);
    }

    public void clear() {
        chains.clear();
        parents.clear();

    }

    public Collection<Event> chain(EventID eventID) {
        return chains.get(eventID);
    }

    public EventID findRoot(final EventID eventId) {
        EventID root = eventId;
        for (String parent = parents.get(root); parent != null && !parent.equals(root.getEventId()); parent = parents.get(root)) {
            root = new EventID(parent, eventId.getPartitionKey());
        }
        return root;
    }

    public void moveToRoot(final EventID from) {
        final EventID to = findRoot(from);
        if (!to.equals(from)) {
            final Collection<Event> events = chains.get(from);
            for (Event event : events) {
                chains.put(to, event);
                chains.remove(from, event);
                logger.info("moved event '{}' from chain '{}' to {}", event, from, to);
            }
        }
    }

    public int chainCount() throws ExecutionException, InterruptedException {
        final MultiMapKeyCountCommand.ResultCollector collector = new MultiMapKeyCountCommand.ResultCollector();
        executionService.submitToAllMembers(new MultiMapKeyCountCommand("chains"), collector);
        return collector.getResult().get();
    }

    public int eventCount() {
        return chains.size();
    }
}
