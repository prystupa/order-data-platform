package com.prystupa.server;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import com.prystupa.core.command.StoreCommand;
import com.prystupa.server.test.HazelcastUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EventChainLinkingTest {

    HazelcastInstance server;
    HazelcastInstance client;
    EventStore store;

    @Before
    public void setup() throws IOException {
        Config config = new ClasspathXmlConfig("event-chain-linking.xml");
        server = StoreApp.bootstrapServer(config);
        client = HazelcastClient.newHazelcastClient(HazelcastUtils.clientConfigFor("event-chain-linking.client.xml", server));
        store = new EventStore(client);
    }

    @After
    public void tearDown() {
        store.clear();
        client.shutdown();
        server.shutdown();
    }

    @Test
    public void ingestSingleEventAddsToParentChain() throws InterruptedException, ExecutionException, TimeoutException {

        // Arrange
        Event event = new Event("2", "1", "P1");

        // Act
        save(event);
        waitChains(1).get(10, TimeUnit.SECONDS);

        // Assert
        Collection<Event> actual = store.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(event));
    }

    @Test
    public void ingestChildAfterRootMovesChildToRootChain() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        Event root = new Event("1", "1", "P1");
        Event child = new Event("2", "1", "P1");
        save(root);

        // Act
        save(child);
        waitChains(1).get(10, TimeUnit.SECONDS);

        // Assert
        Collection<Event> actual = store.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child));
    }

    @Test
    public void ingestRootAfterChildMovesChildToRootChain() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        Event child = new Event("2", "1", "P1");
        Event root = new Event("1", "1", "P1");
        save(child);

        // Act
        save(root);
        waitChains(1).get(10, TimeUnit.SECONDS);

        // Assert
        Collection<Event> actual = store.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child));
    }

    @Test
    public void ingestMissingLinkMergesChainsToRoot() throws InterruptedException, ExecutionException, TimeoutException {
        Event grandChild = new Event("3", "2", "P1");
        Event root = new Event("1", "1", "P1");
        Event child = new Event("2", "1", "P1");
        save(grandChild);
        save(root);
        waitChains(2).get(10, TimeUnit.SECONDS);

        // Act
        save(child);
        waitChains(1).get(10, TimeUnit.SECONDS);

        // Assert
        Collection<Event> actual = store.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child, grandChild));
    }

    private void save(final Event event) {
        client.getExecutorService("default").submitToKeyOwner(new StoreCommand(event), new EventID(event.getEventId(), event.getPrimeId()), new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
                // nothing to do
            }

            @Override
            public void onFailure(Throwable t) {
                // nothing to do
            }
        });
    }

    private CompletableFuture<Void> waitChains(final int expectedChains) {
        return CompletableFuture.runAsync(() -> {
            do {
                Thread.yield();
            } while (client.getMultiMap("chains").keySet().size() != expectedChains);
        });
    }
}
