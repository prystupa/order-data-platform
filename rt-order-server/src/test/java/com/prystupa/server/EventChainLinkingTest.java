package com.prystupa.server;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import com.prystupa.core.EventStore;
import com.prystupa.core.test.HazelcastUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void setup() {
        Config config = new ClasspathXmlConfig("event-chain-linking.xml");
        server = Hazelcast.newHazelcastInstance(config);
        StoreApp.setupEntryListeners(server);

        client = HazelcastClient.newHazelcastClient(HazelcastUtils.clientConfigFor(server));
        store = new EventStore(client);
    }

    @After
    public void tearDown() {
        store.clear();
        client.shutdown();
        server.shutdown();
    }

    @Test
    public void ingestChildAfterRootMovesChildToRootChain() throws InterruptedException, ExecutionException, TimeoutException {
        // Arrange
        Event root = new Event("1", "1", "P1");
        Event child = new Event("2", "1", "P1");
        store.save(root);

        // Act
        store.save(child);
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
        store.save(child);

        // Act
        store.save(root);
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
        store.save(grandChild);
        store.save(root);
        waitChains(2).get(10, TimeUnit.SECONDS);

        // Act
        store.save(child);
        waitChains(1).get(10, TimeUnit.SECONDS);

        // Assert
        Collection<Event> actual = store.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child, grandChild));
    }

    private CompletableFuture<Void> waitChains(final int expectedChains) {
        return CompletableFuture.runAsync(() -> {
            do {
                Thread.yield();
            } while (client.getMultiMap("chains").keySet().size() != expectedChains);
        });
    }
}
