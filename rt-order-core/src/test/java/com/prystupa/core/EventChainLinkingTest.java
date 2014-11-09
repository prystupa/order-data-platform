package com.prystupa.core;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.test.HazelcastUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class EventChainLinkingTest {

    HazelcastInstance server;
    HazelcastInstance client;
    EventIngester ingester;

    @Before
    public void setup() {
        Config config = new ClasspathXmlConfig("event-chain-linking.xml");
        server = Hazelcast.newHazelcastInstance(config);
        client = HazelcastClient.newHazelcastClient(HazelcastUtils.clientConfigFor(server));
        ingester = new EventIngester(client);
    }

    @After
    public void tearDown() {
        ingester.clear();
        client.shutdown();
        server.shutdown();
    }

    @Test
    public void ingestChildAfterRootMovesChildToRootChain() {
        // Arrange
        Event root = new Event("1", "1", "P1");
        Event child = new Event("2", "1", "P1");
        ingester.ingest(root);
        waitIngester();

        // Act
        ingester.ingest(child);
        waitIngester();


        // Assert
        Collection<Event> actual = ingester.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child));
    }

    @Test
    public void ingestRootAfterChildMovesChildToRootChain() {
        // Arrange
        Event child = new Event("2", "1", "P1");
        Event root = new Event("1", "1", "P1");
        ingester.ingest(child);
        waitIngester();

        // Act
        ingester.ingest(root);

        // Assert
        Collection<Event> actual = ingester.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child));
    }

    @Test
    public void ingestMissingLinkMergesChainsToRoot() {
        Event grandChild = new Event("3", "2", "P1");
        Event root = new Event("1", "1", "P1");
        Event child = new Event("2", "1", "P1");
        ingester.ingest(grandChild);
        ingester.ingest(root);
        waitIngester();

        // Act
        ingester.ingest(child);
        waitIngester();

        // Assert
        Collection<Event> actual = ingester.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(root, child, grandChild));
    }

    private void waitIngester() {
        // simple trick to get a chance for all async processing to complete, mainly entry listeners on "chains" and "parents" maps
        // seems to work - if it breaks need to think of more robust synchronization
        client.getMultiMap("chains").size();
        client.getMap("parents").size();
    }
}