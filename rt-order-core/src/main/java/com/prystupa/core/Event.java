package com.prystupa.core;

import com.google.common.base.Objects;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class Event implements DataSerializable {
    private String id;
    private String parentId;
    private String primeId;

    public Event(final String id, final String parentId, String primeId) {
        this();

        this.id = id;
        this.parentId = parentId;
        this.primeId = primeId;
    }

    public Event() {

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

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(parentId);
        out.writeUTF(primeId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        id = in.readUTF();
        parentId = in.readUTF();
        primeId = in.readUTF();
    }
}
