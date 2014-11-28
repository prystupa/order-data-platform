package com.prystupa.core.monitor;

import java.io.Serializable;

public class LoaderStats implements Serializable {

    private final String uuid;
    private final int chains;
    private final int events;

    public LoaderStats(final String uuid, final int chains, final int events) {
        this.uuid = uuid;
        this.chains = chains;
        this.events = events;
    }

    public String getUuid() {
        return uuid;
    }

    public int getChains() {
        return chains;
    }

    public int getEvents() {
        return events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoaderStats that = (LoaderStats) o;

        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
