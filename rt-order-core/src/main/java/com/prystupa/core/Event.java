package com.prystupa.core;

import com.google.common.base.Objects;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import java.io.IOException;

public class Event implements IdentifiedDataSerializable {
    private String eventId;
    private String parentId;
    private String primeId;

    public Event(final String eventId, final String parentId, String primeId) {
        this();

        this.eventId = eventId;
        this.parentId = parentId;
        this.primeId = primeId;
    }

    public Event() {

    }

    public String getEventId() {
        return eventId;
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
                .add("eventID", eventId)
                .add("parentID", parentId)
                .add("primeID", primeId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return eventId.equals(event.eventId);
    }

    @Override
    public int hashCode() {
        return eventId.hashCode();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(eventId);
        out.writeUTF(parentId);
        out.writeUTF(primeId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        eventId = in.readUTF();
        parentId = in.readUTF();
        primeId = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return EventFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return EventFactory.EVENT_TYPE;
    }
}
