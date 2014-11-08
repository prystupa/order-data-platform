package com.prystupa;

import java.io.Serializable;

public class ChainEvent implements Serializable {
    final public String id;
    final public ChainEventKey key;

    public ChainEvent(String id, String parentId, String primeId) {
        this.id = id;
        this.key = new ChainEventKey(parentId, primeId);
    }

    @Override
    public String toString() {
        return "(" + id + ", " + key.getChainId() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChainEvent that = (ChainEvent) o;

        return id.equals(that.id) && key.getChainId().equals(that.key.getChainId());
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + key.getChainId().hashCode();
        return result;
    }
}
