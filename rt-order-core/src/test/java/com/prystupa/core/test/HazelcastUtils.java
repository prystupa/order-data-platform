package com.prystupa.core.test;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastUtils {

    static public ClientConfig clientConfigFor(HazelcastInstance server) {
        ClientConfig config = new ClientConfig();
        String address = server.getLocalEndpoint().getSocketAddress().toString().substring(1); // skip '/' at the start
        config.setNetworkConfig(new ClientNetworkConfig().addAddress(address));
        return config;
    }
}
