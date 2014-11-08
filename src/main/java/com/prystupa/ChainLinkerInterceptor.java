package com.prystupa;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.map.MapInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChainLinkerInterceptor implements MapInterceptor, Serializable, HazelcastInstanceAware {
    final private Logger logger = LoggerFactory.getLogger(ChainLinkerInterceptor.class);
    private transient HazelcastInstance client;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.client = hazelcastInstance;
    }

    @Override
    public Object interceptGet(Object value) {
        return null;
    }

    @Override
    public void afterGet(Object value) {

    }

    @Override
    public Object interceptPut(Object oldValue, Object newValue) {
        final IMap<ChainEventKey, String> chainLookup = client.getMap("chainLookup");
        final IMap<ChainEventKey, List<ChainEvent>> chains = client.getMap("chains");

        ChainEvent event = (ChainEvent) newValue;
        String id = event.id;
        ChainEventKey key = event.key;

        List<ChainEvent> chainEvents = oldValue != null ? (List<ChainEvent>) oldValue : new ArrayList<ChainEvent>();

        String parentChainId = chainLookup.putIfAbsent(key, key.getChainId());
        if (parentChainId == null) {
            parentChainId = key.getChainId();
            String childChainId = chainLookup.putIfAbsent(new ChainEventKey(id, key.getPartitionKey()), parentChainId);
            if (childChainId != null && !childChainId.equals(parentChainId)) {
                List<ChainEvent> childEvents = chains.get(new ChainEventKey(childChainId, key.getPartitionKey()));
                chainEvents.addAll(childEvents);
                logger.info("Merged chain '{}' into '{}'", childChainId, parentChainId);
            }
        }

        chainEvents.add(event);
        logger.info("Added event '{}' to chain '{}'", event, parentChainId);
        return chainEvents;
    }

    @Override
    public void afterPut(Object value) {

    }

    @Override
    public Object interceptRemove(Object removedValue) {
        return null;
    }

    @Override
    public void afterRemove(Object value) {

    }
}