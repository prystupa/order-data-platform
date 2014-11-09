package com.prystupa.core;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.test.HazelcastUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

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
    public void ingestSingleEventAddsToChain() {

        // Arrange
        Event event = new Event("2", "1", "P1");

        // Act
        target.ingest(event);

        // Assert
        Collection<Event> actual = target.chain(new EventID("2", "P1"));
        Assert.assertEquals(Arrays.asList(event), actual);
    }
}
