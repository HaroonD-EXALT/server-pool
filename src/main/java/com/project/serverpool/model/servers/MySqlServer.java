package com.project.serverpool.model.servers;

import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;

public class MySqlServer extends Server {
    public MySqlServer(double memory) {
        setMemory(memory);
        setDatabase(ServerFactory.MYSQL);
//        setState("creating");
    }

    @Override
    public String configDatabase() {
        return "MySql configuration";
    }
}
