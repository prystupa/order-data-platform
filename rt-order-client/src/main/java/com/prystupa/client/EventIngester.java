package com.prystupa.client;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
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
        parents.put(eventID, event.getParentId());
        chains.put(eventID, event);
        logger.info("saved event '{}' to chain '{}'", event, eventID);
    }

    public void clear() {
        chains.clear();
        parents.clear();
    }

    public Collection<Event> chain(EventID eventID) {
        return chains.get(eventID);
    }
}
