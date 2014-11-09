package com.prystupa.core;

import com.hazelcast.core.*;

public class ParentEventListener implements EntryListener<EventID, String>, HazelcastInstanceAware {
    private HazelcastInstance hazelcastInstance;


    @Override
    public void entryAdded(EntryEvent<EventID, String> event) {
        EventID eventID = event.getKey();
        EventID parentEventId = new EventID(event.getValue(), eventID.getPartitionKey());
        EventIngester eventIngester = new EventIngester(hazelcastInstance);
        eventIngester.move(parentEventId, eventID);
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
