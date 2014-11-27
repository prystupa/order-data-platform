package com.prystupa.core;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventChainListener implements EntryListener<EventID, Event>, HazelcastInstanceAware {
    private static Logger logger = LoggerFactory.getLogger(EventChainListener.class);
    private HazelcastInstance hazelcastInstance;
    private static EventStore eventStore;

    @Override
    public void entryAdded(EntryEvent<EventID, Event> event) {
        logger.debug("Event added {}", event.getKey());
        if (eventStore == null) {
            eventStore = new EventStore(hazelcastInstance);
        }
        final EventID eventId = event.getKey();
        eventStore.moveToRoot(eventId);
    }

    @Override
    public void entryRemoved(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void entryUpdated(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void entryEvicted(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void mapEvicted(MapEvent event) {

    }

    @Override
    public void mapCleared(MapEvent event) {

    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
