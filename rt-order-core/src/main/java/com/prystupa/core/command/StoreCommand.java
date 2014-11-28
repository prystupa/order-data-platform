package com.prystupa.core.command;

import akka.actor.ActorRef;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MultiMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StoreCommand implements Runnable, IdentifiedDataSerializable, HazelcastInstanceAware {
    private transient final static Logger logger = LoggerFactory.getLogger(StoreCommand.class);
    private transient ActorRef linker;
    private Event event;
    private transient MultiMap<EventID, Event> chains;

    public StoreCommand(final Event event) {
        this.event = event;
    }

    public StoreCommand(final ActorRef linker) {
        this.linker = linker;
    }

    @Override
    public void run() {
        logger.debug("storing event {}", event);
        chains.put(new EventID(event.getParentId(), event.getPrimeId()), event);
        linker.tell(event, ActorRef.noSender());
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcast) {
        chains = hazelcast.getMultiMap("chains");
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

    @Override
    public int getFactoryId() {
        return CommandFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return CommandFactory.STORE_TYPE;
    }
}
