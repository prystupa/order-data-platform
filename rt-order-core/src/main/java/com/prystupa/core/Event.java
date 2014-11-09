package com.prystupa.core;

import com.google.common.base.Objects;

import java.io.Serializable;

public class Event implements Serializable {
    private final String id;
    private final String parentId;
    private final String primeId;

    public Event(final String id, final String parentId, String primeId) {
        this.id = id;
        this.parentId = parentId;
        this.primeId = primeId;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getPrimeId() {
        return primeId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("parentId", parentId)
                .add("primeId", primeId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
