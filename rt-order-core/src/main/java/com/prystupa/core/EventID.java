package com.prystupa.core;

import com.google.common.base.Objects;
import com.hazelcast.core.PartitionAware;

import java.io.Serializable;

public class EventID implements PartitionAware<String>, Serializable {

    private final String id;
    private final String primeId;

    public EventID(String id, String primeId) {
        this.id = id;
        this.primeId = primeId;
    }

    public String getId() {
        return id;
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

        return id.equals(eventID.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("primeId", primeId)
                .toString();
    }
}
