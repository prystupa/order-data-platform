package com.prystupa.core.command;

import akka.actor.ActorRef;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CommandFactory implements DataSerializableFactory {
    public static final int FACTORY_ID = 2;
    public static final int STORE_TYPE = 1;
    public static final int MM_KEY_COUNT_TYPE = 2;
    public static final int LOADER_STATS_TYPE = 3;
    private final CompletableFuture<ActorRef> linkerPromise;

    public CommandFactory(final CompletableFuture<ActorRef> linkerPromise) {
        this.linkerPromise = linkerPromise;
    }

    @Override
    public IdentifiedDataSerializable create(int typeId) {
        switch (typeId) {
            case STORE_TYPE:
                try {
                    return new StoreCommand(linkerPromise.get(0, TimeUnit.MILLISECONDS));
                } catch (Exception e) {
                    return null;
                }
            case MM_KEY_COUNT_TYPE:
                return new MultiMapKeyCountCommand();
            case LOADER_STATS_TYPE:
                return new LoaderStatsCommand();
        }

        return null;
    }
}
