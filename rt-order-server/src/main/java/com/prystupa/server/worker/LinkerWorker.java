package com.prystupa.server.worker;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkerWorker extends UntypedActor {
    private static Logger logger = LoggerFactory.getLogger(LinkerWorker.class);
    private final EventStore eventStore;

    public LinkerWorker(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public static Props props(final EventStore eventStore) {
        return Props.create(LinkerWorker.class, () -> new LinkerWorker(eventStore));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof EventID) {
            EventID eventID = (EventID) message;
            logger.debug("Event added {}", eventID);
            eventStore.moveToRoot(eventID);
        } else {
            unhandled(message);
        }
    }
}
