package com.prystupa.core;

import com.hazelcast.core.*;

public class EventChainListener implements EntryListener<EventID, Event>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;
    private static EventIngester eventIngester;

    @Override
    public void entryAdded(EntryEvent<EventID, Event> event) {
        if (eventIngester == null) {
            eventIngester = new EventIngester(hazelcastInstance);
        }
        final EventID eventId = event.getKey();
        eventIngester.moveToRoot(eventId);
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
