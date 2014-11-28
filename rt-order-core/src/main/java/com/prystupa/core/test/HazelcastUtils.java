package com.prystupa.core.test;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;

public class HazelcastUtils {

    static public ClientConfig clientConfigFor(final String xmlConfig, HazelcastInstance server) throws IOException {
        final ClientConfig config = new XmlClientConfigBuilder(xmlConfig).build();
        final String address = server.getLocalEndpoint().getSocketAddress().toString().substring(1); // skip '/' at the start
        config.setNetworkConfig(new ClientNetworkConfig().addAddress(address));
        return config;
    }
}
