package com.prystupa.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.prystupa.core.command.CommandFactory;
import com.prystupa.server.linker.LinkerDispatcher;
import com.prystupa.server.linker.LinkerWorker;

import java.util.concurrent.CompletableFuture;

public class StoreApp {

    public static void main(String[] args) {

        bootstrapServer(new ClasspathXmlConfig("hazelcast.xml"));
    }

    public static HazelcastInstance bootstrapServer(final Config config) {
        final ActorSystem system = ActorSystem.create();
        final CompletableFuture<ActorRef> linkerPromise = new CompletableFuture<>();
        config.getSerializationConfig().addDataSerializableFactory(CommandFactory.FACTORY_ID, new CommandFactory(linkerPromise));
        final HazelcastInstance server = Hazelcast.newHazelcastInstance(config);

        linkerPromise.complete(system.actorOf(LinkerDispatcher.props(LinkerWorker.props(server))));
        return server;
    }
}
