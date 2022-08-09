package com.project.serverpool.model.servers;

import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;

public class OracleServer extends Server {
    public OracleServer(double memory) {
        setMemory(memory);
        setDatabase(ServerFactory.ORACLE);
//        setState("creating");
    }

    @Override
    public String configDatabase() {
        return "Oracle configuration";
    }
}
