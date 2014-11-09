package com.prystupa.core;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

public class EventChainListenerTest {

    HazelcastInstance server;
    HazelcastInstance client;
    EventIngester ingester;

    @Before
    public void setup() {
        Config config = new ClasspathXmlConfig("event-chain-listener.xml");
        server = Hazelcast.newHazelcastInstance(config);
        client = HazelcastClient.newHazelcastClient();
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

        // Act
        ingester.ingest(child);

        // Assert
        Collection<Event> actual = ingester.chain(new EventID("1", "P1"));
        Assert.assertEquals(Arrays.asList(root, child), actual);
    }

    @Test
    public void ingestRootAfterChildMovesChildToRootChain() {
        // Arrange
        Event child = new Event("2", "1", "P1");
        Event root = new Event("1", "1", "P1");
        ingester.ingest(child);

        // Act
        ingester.ingest(root);

        // Assert
        Collection<Event> actual = ingester.chain(new EventID("1", "P1"));
        Assert.assertEquals(Arrays.asList(root, child), actual);
    }
}
