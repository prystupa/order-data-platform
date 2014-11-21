package com.prystupa.client.ingest;

import akka.actor.UntypedActor;
import com.prystupa.core.Event;
import com.prystupa.core.EventIngester;

public class Ingester extends UntypedActor {
    private final EventIngester ingester;

    public Ingester(final EventIngester ingester) {
        this.ingester = ingester;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Event) {
            final Event event = (Event) message;
            ingester.ingest(event);
            getContext().stop(self());
        } else {
            unhandled(message);
        }
    }
}
