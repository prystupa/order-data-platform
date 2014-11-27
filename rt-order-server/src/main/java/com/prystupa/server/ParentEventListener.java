package com.prystupa.server;

import com.hazelcast.core.*;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentEventListener implements EntryListener<EventID, String>, HazelcastInstanceAware {
    private static Logger logger = LoggerFactory.getLogger(ParentEventListener.class);
    private HazelcastInstance hazelcastInstance;
    private static EventStore eventStore;

    @Override
    public void entryAdded(EntryEvent<EventID, String> event) {
        logger.debug("Parent added {}", event.getKey());
        if (eventStore == null) {
            eventStore = new EventStore(hazelcastInstance);
        }
        EventID eventID = event.getKey();
        eventStore.moveToRoot(eventID);
    }

    @Override
    public void entryRemoved(EntryEvent<EventID, String> event) {

    }

    @Override
    public void entryUpdated(EntryEvent<EventID, String> event) {

    }

    @Override
    public void entryEvicted(EntryEvent<EventID, String> event) {

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
