package com.prystupa.core.command;

import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IntegerCountCollector implements MultiExecutionCallback {
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
