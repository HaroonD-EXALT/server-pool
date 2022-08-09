package com.project.serverpool.model.servers;

import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;

public class MongoDBServer extends Server {
    public MongoDBServer(double memory) {
        setMemory(memory);
        setDatabase(ServerFactory.MONGODB);
//        setState("creating");
    }

    @Override
    public String configDatabase() {
        return "MongoDB configuration";
    }
}
