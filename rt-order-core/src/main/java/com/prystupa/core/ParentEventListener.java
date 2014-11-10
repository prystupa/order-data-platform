package com.prystupa.core;

import com.hazelcast.core.*;

public class ParentEventListener implements EntryListener<EventID, String>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;
    private static EventIngester eventIngester;

    @Override
    public void entryAdded(EntryEvent<EventID, String> event) {
        if (eventIngester == null) {
            eventIngester = new EventIngester(hazelcastInstance);
        }
        EventID eventID = event.getKey();
        eventIngester.moveToRoot(eventID);
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
