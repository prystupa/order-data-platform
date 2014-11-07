package com.prystupa;

import com.hazelcast.core.Hazelcast;

public class StoreApp {

    public static void main(String[] args) {

        Hazelcast.newHazelcastInstance();
    }
}
