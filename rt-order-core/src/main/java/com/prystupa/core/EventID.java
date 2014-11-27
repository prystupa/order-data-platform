package com.prystupa.core;

import com.google.common.base.Objects;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class EventID implements PartitionAware<String>, DataSerializable {

    private String id;
    private String primeId;

    public EventID(String id, String primeId) {
        this();

        this.id = id;
        this.primeId = primeId;
    }

    public EventID() {

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

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(primeId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        id = in.readUTF();
        primeId = in.readUTF();
    }
}
