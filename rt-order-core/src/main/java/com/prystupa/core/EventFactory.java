package com.prystupa.core;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class EventFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 1;
    public static final int EVENT_ID_TYPE = 1;
    public static final int EVENT_TYPE = 2;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        switch (typeId) {
            case EVENT_ID_TYPE:
                return new EventID();
            case EVENT_TYPE:
                return new Event();
        }
        return null;
    }
}
