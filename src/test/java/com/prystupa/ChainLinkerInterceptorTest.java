package com.prystupa;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ChainLinkerInterceptorTest {
    ChainLinkerInterceptor target;
    HazelcastInstance hazelcastInstance1;
    HazelcastInstance hazelcastInstance2;
    HazelcastInstance client;
    IMap<ChainEventKey, ChainEvent> input;
    IMap<ChainEventKey, List<ChainEvent>> stored;

    @Before
    public void setup() {
        Config config = new Config();
//        config.getNetworkConfig().setJoin(new JoinConfig().setMulticastConfig(new MulticastConfig().setEnabled(false)));
        hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
//        String address = hazelcastInstance1.getLocalEndpoint().getSocketAddress().toString().substring(1);
//
//        JoinConfig joinConfig = new JoinConfig().setMulticastConfig(new MulticastConfig().setEnabled(false))
//                .setTcpIpConfig(new TcpIpConfig().addMember(address));
        hazelcastInstance2 = Hazelcast.newHazelcastInstance();

        ClientConfig clientConfig = new ClientConfig();
        client = HazelcastClient.newHazelcastClient(clientConfig);

        target = new ChainLinkerInterceptor();
        input = client.getMap("chains");
        stored = client.getMap("chains");

        input.addInterceptor(target);
    }

    @After
    public void tearDown() {
        client.shutdown();
        hazelcastInstance1.shutdown();
        hazelcastInstance2.shutdown();
    }

    @Test
    public void insertingRoot() {
        // Arrange
        ChainEvent root = new ChainEvent("1", "1", "P");

        // Act
        input.put(root.key, root);

        // Assert
        Assert.assertEquals(Arrays.asList(root), stored.get(root.key));
    }

    @Test
    public void insertChildAfterRoot() {
        // Arrange
        ChainEvent root = new ChainEvent("1", "1", "P");
        ChainEvent child = new ChainEvent("2", "1", "P");
        input.put(root.key, root);

        // Act
        input.put(child.key, child);

        // Assert
        Assert.assertEquals(Arrays.asList(root, child), stored.get(root.key));
        Assert.assertEquals(null, stored.get(new ChainEventKey(child.id, child.key.getPartitionKey())));
    }

    @Test
    public void separateChains() {
        // Arrange
        ChainEvent root = new ChainEvent("1", "1", "P");
        ChainEvent grandChild = new ChainEvent("3", "2", "P");

        // Act
        input.put(root.key, root);
        input.put(grandChild.key, grandChild);

        // Assert
        Assert.assertEquals(Arrays.asList(root), stored.get(root.key));
        Assert.assertEquals(Arrays.asList(grandChild), stored.get(grandChild.key));
        Assert.assertEquals(null, stored.get(new ChainEventKey(grandChild.id, grandChild.key.getPartitionKey())));
    }

    @Test
    public void mergeChains() {
        // Arrange
        ChainEvent root = new ChainEvent("1", "1", "P");
        ChainEvent grandChild = new ChainEvent("3", "2", "P");
        ChainEvent child = new ChainEvent("2", "1", "P");

        input.put(root.key, root);
        input.put(grandChild.key, grandChild);

        // Act
        input.put(child.key, child);

        // Assert
    }
}
