package com.prystupa.server;

import akka.actor.ActorRef;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventChainListener implements EntryListener<EventID, Event> {
    private static Logger logger = LoggerFactory.getLogger(EventChainListener.class);
    private final ActorRef linker;

    public EventChainListener(ActorRef linker) {
        this.linker = linker;
    }

    @Override
    public void entryAdded(EntryEvent<EventID, Event> event) {
        final EventID eventID = event.getKey();
        logger.debug("Event added {}", eventID);
        linker.tell(eventID, ActorRef.noSender());
    }

    @Override
    public void entryRemoved(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void entryUpdated(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void entryEvicted(EntryEvent<EventID, Event> event) {

    }

    @Override
    public void mapEvicted(MapEvent event) {

    }

    @Override
    public void mapCleared(MapEvent event) {

    }
}
