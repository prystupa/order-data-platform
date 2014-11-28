package com.prystupa.core.command;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class CommandFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 2;
    public static final int STORE_TYPE = 1;
    public static final int MM_KEY_COUNT_TYPE = 2;

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        switch (typeId) {
            case STORE_TYPE:
                return new StoreCommand();
            case MM_KEY_COUNT_TYPE:
                return new MultiMapKeyCountCommand();
        }

        return null;
    }
}
