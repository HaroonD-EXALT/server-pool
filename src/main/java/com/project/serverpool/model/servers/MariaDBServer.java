package com.project.serverpool.model.servers;

import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;

public class MariaDBServer extends Server {
    public MariaDBServer(double memory) {
        setMemory(memory);
        setDatabase(ServerFactory.MARIADB);
//        setState("creating");
    }

    @Override
    public String configDatabase() {
        return "MariaDB configuration";
    }
}
