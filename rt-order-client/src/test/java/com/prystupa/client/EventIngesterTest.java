package com.prystupa.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.Event;
import com.prystupa.core.EventID;
import org.junit.*;

import java.util.Arrays;
import java.util.Collection;

public class EventIngesterTest {

    static HazelcastInstance server;
    HazelcastInstance client;
    EventIngester target;

    @BeforeClass
    public static void setupClass() {
        server = Hazelcast.newHazelcastInstance();
    }

    @AfterClass
    public static void tearDownClass() {
        server.shutdown();
    }

    @Before
    public void setup() {
        client = HazelcastClient.newHazelcastClient();
        target = new EventIngester(client);
    }

    @After
    public void tearDown() {
        target.clear();
        client.shutdown();
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
