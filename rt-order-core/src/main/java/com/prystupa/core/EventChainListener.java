package com.prystupa.core;

import com.hazelcast.core.*;

public class EventChainListener implements EntryListener<EventID, Event>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;


    @Override
    public void entryAdded(EntryEvent<EventID, Event> event) {
        EventIngester eventIngester = new EventIngester(hazelcastInstance);
        final EventID eventId = event.getKey();
        final String keyParent = eventIngester.getParent(eventId);
        if (keyParent != null) {
            eventIngester.move(new EventID(keyParent, eventId.getPartitionKey()), eventId);
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
