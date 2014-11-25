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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EventIngesterTest {

    HazelcastInstance server;
    HazelcastInstance client;
    EventIngester target;

    @Before
    public void setup() {
        Config config = new ClasspathXmlConfig("event-ingester.xml");
        server = Hazelcast.newHazelcastInstance(config);
        client = HazelcastClient.newHazelcastClient(HazelcastUtils.clientConfigFor(server));
        target = new EventIngester(client);
    }

    @After
    public void tearDown() {
        target.clear();
        client.shutdown();
        server.shutdown();
    }

    @Test
    public void ingestSingleEventAddsToParentChain() throws InterruptedException, ExecutionException {

        // Arrange
        Event event = new Event("2", "1", "P1");

        // Act
        CompletableFuture<Object> future = target.ingest(event);
        future.get();

        // Assert
        Collection<Event> actual = target.chain(new EventID("1", "P1"));
        Assert.assertThat(actual, Matchers.containsInAnyOrder(event));
    }
}
