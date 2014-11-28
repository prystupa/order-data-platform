package com.prystupa.server.linker;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.prystupa.core.Event;

import java.util.HashMap;
import java.util.Map;

public class LinkerDispatcher extends UntypedActor {

    private final Props workerProps;
    private final Map<String, ActorRef> workers = new HashMap<>();

    public LinkerDispatcher(final Props workerProps) {
        this.workerProps = workerProps;
    }

    public static Props props(final Props worker) {
        return Props.create(LinkerDispatcher.class, () -> new LinkerDispatcher(worker));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Event) {
            Event event = (Event) message;
            String key = event.getPrimeId();
            ActorRef worker = workers.get(key);
            if (worker == null) {
                worker = getContext().actorOf(workerProps);
                workers.put(key, worker);
            }
            worker.forward(message, getContext());
        } else {
            unhandled(message);
        }
    }
}
