package com.prystupa;

import com.hazelcast.core.PartitionAware;

import java.io.Serializable;

public class ChainEventKey implements Serializable, PartitionAware<String> {

    private final String chainId;
    private final String partitionId;

    public ChainEventKey(final String chainId, final String partitionId) {
        this.chainId = chainId;
        this.partitionId = partitionId;
    }

    @Override
    public String getPartitionKey() {
        return partitionId;
    }

    public String getChainId() {
        return chainId;
    }
}
