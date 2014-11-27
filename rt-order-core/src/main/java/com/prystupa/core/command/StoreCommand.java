package com.prystupa.core.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.prystupa.core.Event;
import com.prystupa.core.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class StoreCommand implements Runnable, Serializable, HazelcastInstanceAware {
    private transient final static Logger logger = LoggerFactory.getLogger(StoreCommand.class);
    private final Event event;
    private transient EventStore store;

    public StoreCommand(final Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        logger.debug("storing event {}", event);
        store.save(event);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {

        store = new EventStore(hazelcastInstance);
    }
}
