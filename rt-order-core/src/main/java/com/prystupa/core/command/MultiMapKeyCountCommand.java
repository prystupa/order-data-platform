package com.prystupa.core.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class MultiMapKeyCountCommand implements Callable<Integer>, DataSerializable, HazelcastInstanceAware {

    private String map;
    private transient HazelcastInstance hazelcastInstance;

    public MultiMapKeyCountCommand(String map) {
        this();
        this.map = map;
    }

    public MultiMapKeyCountCommand() {

    }

    @Override
    public Integer call() throws Exception {

        return hazelcastInstance.getMultiMap(map).localKeySet().size();
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcastInstance) {

        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(map);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        map = in.readUTF();
    }

    public static class ResultCollector implements MultiExecutionCallback {
        private final CompletableFuture<Integer> result = new CompletableFuture<>();

        @Override
        public void onResponse(Member member, Object value) {
            // noting to do
        }

        @Override
        public void onComplete(Map<Member, Object> values) {
            int count = values.values().stream().map(o -> (Integer) o).reduce(0, (n1, n2) -> n1 + n2);
            result.complete(count);
        }

        public CompletableFuture<Integer> getResult() {
            return result;
        }
    }
}
