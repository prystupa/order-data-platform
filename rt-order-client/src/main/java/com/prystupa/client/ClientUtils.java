package com.prystupa.client;

import com.hazelcast.client.config.ClientAwsConfig;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;

import java.io.IOException;

public class ClientUtils {
    public static ClientConfig buildConfig() throws IOException {
        final ClientConfig config = new XmlClientConfigBuilder("hazelcast-client.xml").build();

        if ("true".equals(System.getProperty("aws.enabled", "false"))) {
            final ClientAwsConfig awsConfig = config.getNetworkConfig().getAwsConfig();
            awsConfig.setEnabled(true);
            awsConfig.setAccessKey(System.getProperty("aws.access-key"));
            awsConfig.setSecretKey(System.getProperty("aws.secret-key"));
        }

        return config;
    }
}
