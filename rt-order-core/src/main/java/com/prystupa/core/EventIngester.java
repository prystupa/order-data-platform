package com.prystupa.core;

import com.hazelcast.core.*;
import com.prystupa.core.command.StoreCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EventIngester {

    private final Logger logger = LoggerFactory.getLogger(EventIngester.class);
    private final IMap<EventID, String> parents;
    private final MultiMap<EventID, Event> chains;
    private final IExecutorService executorService;

    public EventIngester(final HazelcastInstance client) {
        parents = client.getMap("parents");
        chains = client.getMultiMap("chains");
        executorService = client.getExecutorService("default");
    }

    public CompletableFuture<Object> ingest(final Event event) throws InterruptedException {

        final CompletableFuture<Object> result = new CompletableFuture<>();
        executorService.submitToKeyOwner(new StoreCommand(event), event.getPrimeId(), new ExecutionCallback<Object>() {
            @Override
            public void onResponse(Object response) {
                logger.debug("Submitted event");
                result.complete(response);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Failed submitting event", t);
                result.completeExceptionally(t);
            }
        });

        return result;
    }

    public void clear() {
        chains.clear();
        parents.clear();

    }

    public Collection<Event> chain(EventID eventID) {
        return chains.get(eventID);
    }

    public void moveToRoot(final EventID from) {
        final EventID to = EventUtils.getRoot(from, parents);
        if (!to.equals(from)) {
            final Collection<Event> events = chains.get(from);
            for (Event event : events) {
                chains.put(to, event);
                chains.remove(from, event);
                logger.info("moved event '{}' from chain '{}' to {}", event, from, to);
            }
        }
    }

    public int chainCount() {
        return chains.keySet().size();
    }
}
