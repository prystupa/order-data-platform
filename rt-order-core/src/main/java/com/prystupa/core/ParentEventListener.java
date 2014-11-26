package com.prystupa.core;

import com.hazelcast.core.*;

public class ParentEventListener implements EntryListener<EventID, String>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;
    private static EventStore eventStore;

    @Override
    public void entryAdded(EntryEvent<EventID, String> event) {
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
