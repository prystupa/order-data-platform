package com.prystupa.core.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.prystupa.core.Event;
import com.prystupa.core.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StoreCommand implements Runnable, DataSerializable, HazelcastInstanceAware {
    private transient final static Logger logger = LoggerFactory.getLogger(StoreCommand.class);
    private Event event;
    private transient EventStore store;

    public StoreCommand(final Event event) {
        this();
        this.event = event;
    }

    public StoreCommand() {

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

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        event.writeData(out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        event = new Event();
        event.readData(in);
    }
}
