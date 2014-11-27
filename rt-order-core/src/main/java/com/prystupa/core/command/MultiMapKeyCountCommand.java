package com.prystupa.core.command;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class MultiMapKeyCountCommand implements Callable<Integer>, Serializable, HazelcastInstanceAware {

    private final String map;
    private HazelcastInstance hazelcastInstance;

    public MultiMapKeyCountCommand(String map) {
        this.map = map;
    }

    @Override
    public Integer call() throws Exception {

        return hazelcastInstance.getMultiMap(map).localKeySet().size();
    }

    @Override
    public void setHazelcastInstance(final HazelcastInstance hazelcastInstance) {

        this.hazelcastInstance = hazelcastInstance;
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
