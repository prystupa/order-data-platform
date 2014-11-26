package com.prystupa.core.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class StoreCommand implements Runnable, Serializable, HazelcastInstanceAware {
    private transient static final Logger logger = LoggerFactory.getLogger(StoreCommand.class);
    private final Event event;
    private transient IMap<EventID, String> parents;
    private transient MultiMap<EventID, Event> chains;

    public StoreCommand(final Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        final EventID eventID = new EventID(event.getId(), event.getPrimeId());
        parents.put(eventID, event.getParentId());

        final EventID rootID = EventUtils.getRoot(eventID, parents);
        chains.put(rootID, event);
        logger.debug("saved event '{}' to chain '{}'", event, rootID);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {

        parents = hazelcastInstance.getMap("parents");
        chains = hazelcastInstance.getMultiMap("chains");
    }
}
