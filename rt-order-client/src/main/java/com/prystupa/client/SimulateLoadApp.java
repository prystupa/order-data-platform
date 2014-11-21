package com.prystupa.client;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.prystupa.client.ingest.LoadSimulator;

import java.io.IOException;

public class SimulateLoadApp {

    public static void main(String[] args) throws IOException {

        ActorSystem system = ActorSystem.create("LoadSimulator");
        system.actorOf(Props.create(LoadSimulator.class), "simulator");
    }
}
