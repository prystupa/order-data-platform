package com.prystupa.core;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventStore {

    private final Logger logger = LoggerFactory.getLogger(EventStore.class);
    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;


    public EventStore(final HazelcastInstance client) {
        parents = client.getMap("parents");
        chains = client.getMultiMap("chains");
    }

    public void save(final Event event) {
        final EventID eventID = new EventID(event.getId(), event.getPrimeId());
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
        for (String parent = parents.get(root); parent != null && !parent.equals(root.getId()); parent = parents.get(root)) {
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

    public int chainCount() {
        return chains.keySet().size();
    }
}
