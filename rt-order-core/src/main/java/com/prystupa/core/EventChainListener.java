package com.prystupa.core;

import com.hazelcast.core.*;

public class EventChainListener implements EntryListener<EventID, Event>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;


    @Override
    public void entryAdded(EntryEvent<EventID, Event> event) {
        EventIngester eventIngester = new EventIngester(hazelcastInstance);
        final EventID key = event.getKey();
        final String keyParent = eventIngester.getParent(key);
        if (keyParent != null && !key.getId().equals(keyParent)) {
            eventIngester.move(new EventID(keyParent, key.getPartitionKey()), key);
        }
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
