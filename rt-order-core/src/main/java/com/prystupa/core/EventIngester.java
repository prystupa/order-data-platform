package com.prystupa.core;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventIngester {

    private final Logger logger = LoggerFactory.getLogger(EventIngester.class);
    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;

    public EventIngester(final HazelcastInstance client) {
        parents = client.getMap("parents");
        chains = client.getMultiMap("chains");
    }

    public void ingest(final Event event) {

        final EventID eventID = new EventID(event.getId(), event.getPrimeId());
        final EventID parentEventID = new EventID(event.getParentId(), event.getPrimeId());
        parents.put(eventID, event.getParentId());
        chains.put(parentEventID, event);
        logger.info("saved event '{}' to chain '{}'", event, parentEventID);
    }

    public void clear() {
        chains.clear();
        parents.clear();

    }

    public Collection<Event> chain(EventID eventID) {
        return chains.get(eventID);
    }

    public String getParent(final EventID eventId) {
        return parents.get(eventId);
    }

    public void move(EventID to, EventID from) {
        if (!to.equals(from)) {
            final Collection<Event> events = chains.get(from);
            for (Event event : events) {
                chains.put(to, event);
                chains.remove(from, event);
                logger.info("moved event '{}' from chain '{}' to {}", event, from, to);
            }
        }
    }
}
