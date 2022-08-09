package com.project.serverpool.model.servers;

import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;

public class AerospikeServer extends Server {
    public AerospikeServer(double memory) {
        setMemory(memory);
        setDatabase(ServerFactory.AEROSPIKE);
//        setState("creating");
    }

    @Override
    public String configDatabase() {
        return "Aerospike configuration";
    }
}
