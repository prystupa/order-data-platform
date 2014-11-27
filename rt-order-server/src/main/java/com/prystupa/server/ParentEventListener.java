package com.prystupa.server;

import akka.actor.ActorRef;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.prystupa.core.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentEventListener implements EntryListener<EventID, String> {
    private static Logger logger = LoggerFactory.getLogger(ParentEventListener.class);
    private final ActorRef linker;

    public ParentEventListener(final ActorRef linker) {
        this.linker = linker;
    }

    @Override
    public void entryAdded(EntryEvent<EventID, String> event) {
        final EventID eventID = event.getKey();
        logger.debug("Parent added {}", event.getKey());
        linker.tell(eventID, ActorRef.noSender());
    }

    @Override
    public void entryRemoved(EntryEvent<EventID, String> event) {

    }

    @Override
    public void entryUpdated(EntryEvent<EventID, String> event) {

    }

    @Override
    public void entryEvicted(EntryEvent<EventID, String> event) {

    }

    @Override
    public void mapEvicted(MapEvent event) {

    }

    @Override
    public void mapCleared(MapEvent event) {

    }
}
