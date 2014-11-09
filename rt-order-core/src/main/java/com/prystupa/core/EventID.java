package com.prystupa.core;

import com.hazelcast.core.PartitionAware;

import java.io.Serializable;

public class EventID implements PartitionAware<String>, Serializable {

    private final String eventId;
    private final String primeId;

    public EventID(String eventId, String primeId) {
        this.eventId = eventId;
        this.primeId = primeId;
    }

    @Override
    public String getPartitionKey() {
        return primeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventID eventID = (EventID) o;

        return eventId.equals(eventID.eventId);

    }

    @Override
    public int hashCode() {
        return eventId.hashCode();
    }
}
