package com.project.serverpool.service;

import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;

import java.util.List;

public interface IServerService {

    public List<Server> getAllServerPools();

    public Server locateServer(Client client) throws Exception;

    public Server createNewServer(Client client) throws Exception;

    public void deleteAllServers();

    public void deleteServer(String id);

}
