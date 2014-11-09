package com.prystupa.core;

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
}
