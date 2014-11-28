package com.prystupa.core.command;

import com.hazelcast.core.Client;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.prystupa.core.monitor.LoaderStats;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class LoaderStatsCommand implements Callable<Set<LoaderStats>>, IdentifiedDataSerializable, HazelcastInstanceAware {

    private transient HazelcastInstance hazelcastInstance;

    @Override
    public Set<LoaderStats> call() throws Exception {

        final Set<String> connectedClients = hazelcastInstance.getClientService().getConnectedClients()
                .stream()
                .map(Client::getUuid)
                .collect(Collectors.toSet());

        return hazelcastInstance.<LoaderStats>getSet("loaderStats").stream()
                .filter(loaderStats -> connectedClients.contains(loaderStats.getUuid()))
                .collect(Collectors.toSet());
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcastInstance) {

        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    @Override
    public int getFactoryId() {
        return CommandFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return CommandFactory.LOADER_STATS_TYPE;
    }

    public static class ResultCollector extends SetCollector<LoaderStats> {

    }
}
