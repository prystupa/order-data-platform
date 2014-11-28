package com.prystupa.core.command;

import com.hazelcast.core.Member;
import com.hazelcast.core.MultiExecutionCallback;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SetCollector<T> implements MultiExecutionCallback {
    private final CompletableFuture<Set<T>> result = new CompletableFuture<>();

    @Override
    public void onResponse(Member member, Object value) {
        // noting to do
    }

    @Override
    public void onComplete(Map<Member, Object> values) {
        final Set<T> set = values.values().stream().map(this::cast).flatMap(Collection::stream).collect(Collectors.toSet());
        result.complete(set);
    }

    public CompletableFuture<Set<T>> getResult() {
        return result;
    }

    @SuppressWarnings("unchecked")
    private Set<T> cast(final Object o) {
        return (Set<T>) o;
    }
}
