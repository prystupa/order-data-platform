package com.prystupa.core;

import com.google.common.base.Objects;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import java.io.IOException;

public class EventID implements PartitionAware<String>, IdentifiedDataSerializable {

    private String eventId;
    private String primeId;

    public EventID(String eventId, String primeId) {
        this();

        this.eventId = eventId;
        this.primeId = primeId;
    }

    public EventID() {

    }

    public String getEventId() {
        return eventId;
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("eventID", eventId)
                .add("primeID", primeId)
                .toString();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(eventId);
        out.writeUTF(primeId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        eventId = in.readUTF();
        primeId = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return EventFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return EventFactory.EVENT_ID_TYPE;
    }
}
