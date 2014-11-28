package com.prystupa.server.linker;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkerWorker extends UntypedActor {
    private static Logger logger = LoggerFactory.getLogger(LinkerWorker.class);
    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;

    public LinkerWorker(final HazelcastInstance hazelcast) {
        parents = hazelcast.getMap("parents");
        chains = hazelcast.getMultiMap("chains");
    }

    public static Props props(final HazelcastInstance hazelcast) {
        return Props.create(LinkerWorker.class, () -> new LinkerWorker(hazelcast));
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof Event) {
            final Event event = (Event) message;
            logger.debug("linking event {}", event);

            final EventID eventID = new EventID(event.getEventId(), event.getPrimeId());
            final EventID parent = new EventID(event.getParentId(), event.getPrimeId());
            final EventID root = findRoot(parent);
            parents.put(eventID, root.getEventId());
            if (!root.equals(parent)) {
                move(event, parent, root);
            }

            if (!root.equals(eventID)) {
                chains.get(eventID).stream().forEach(e -> move(e, eventID, root));
            }

        } else {
            unhandled(message);
        }
    }

    private void move(final Event event, final EventID from, final EventID to) {
        chains.put(to, event);
        chains.remove(from, event);
        logger.debug("moved event {} from {} to {}");
    }

    private EventID findRoot(final EventID eventId) {
        EventID root = eventId;
        for (String parent = parents.get(root); parent != null && !parent.equals(root.getEventId()); parent = parents.get(root)) {
            root = new EventID(parent, eventId.getPartitionKey());
        }
        return root;
    }
}
