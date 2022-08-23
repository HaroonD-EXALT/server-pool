package com.project.serverpool.service;

import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.repositories.ClientRepository;
import com.project.serverpool.repositories.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ServerService implements IServerService {

    private final ServerRepository serverRepository;
    private ClientRepository clientRepository;

    @Autowired
    public ServerService(ServerRepository serverRepository, ClientRepository clientRepository) {
        this.serverRepository = serverRepository;
        this.clientRepository = clientRepository;
    }

    /*
            hash(server):{
                            "server": Server,
                         }
    */
    private static final Map<Integer, Map<String, Object>> waitingList = new HashMap<>();

    @Override
    public List<Server> getAllServerPools() {
        return StreamSupport.stream(serverRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Server locateServer(Client client) throws Exception {
        Server server;
        boolean isRecourseLocatedInCreatingSate = false;
        clientRepository.save(client);
        // synchronized on serverRepository to ensure that at most there is one tread search on the database
        synchronized (serverRepository) {
            List<Server> servers = serverRepository.findByCapacityIsGreaterThanEqualAndStateEqualsOrderByCapacityAsc(client.getMemory(), "active");
            System.out.println("*****************");
            for (Server se:
                 servers) {
                System.out.println(se);
            }
            System.out.println("*****************");
            server = servers.stream().findFirst().orElse(null);
            if (server != null) {
                //there is available recourse in the current active servers
                System.out.println("locate new Resource: "+client);
                server.setCapacity(server.getCapacity() - client.getMemory());
                server.addClient(client);
                //update server
                serverRepository.save(server);
            }
        }


        //no available recourse in the current active servers
        //check the servers in the creation state
        if (server == null) {
            //loop over waiting list array to check if is there available resource in the creating state servers
            for (Map.Entry<Integer, Map<String, Object>> entry : waitingList.entrySet()) {
                //ensure that only one thread is read/write the list
                //to read the most recent data from the list
                //#TODO:Sort the waiting list to get the best result
                synchronized (waitingList) {
                    int serverKey = entry.getKey();
                    Map<String, Object> serverData = entry.getValue();
                    Server entryServer = (Server) serverData.get("server");
                    //if the current server has available recourse
                    if (entryServer.getCapacity() >= client.getMemory()) {
                        long leftRecourse = entryServer.getCapacity() - client.getMemory();
                        List<Client> clients = entryServer.getClients();
                        clients.add(client);
                        entryServer.setCapacity(leftRecourse);
                        entryServer.setClients(clients);
                        serverData.put("server", entryServer);
                        System.out.println("in the waiting list: "+client);
                        waitingList.put(serverKey, serverData);
                        // #TODO:solve return issue
                        //server in the creating state
                        isRecourseLocatedInCreatingSate = true;
                        server = entryServer;
                        break;
                    }
                }
            }
            // current waiting list array do not have available resource in the creating state servers
            // spin new server
            if (isRecourseLocatedInCreatingSate) {
                //wait until finish creating
                while (waitingList.get(server.hashCode()) != null) {
                }
                return server;

            } else {
                server = createNewServer(client);
            }

        }
        return server;

    }


    @Override
    public synchronized Server createNewServer(Client client) throws Exception {
        try {
            System.out.println("create new server: "+client);
            Server server = serverRepository.save(new Server());
            // adding new server to waiting list
            synchronized (waitingList) {
                server.setCapacity(server.getCapacity() - client.getMemory());
                waitingList.put(server.hashCode(), new HashMap() {{
                    put("server", server);
                }});
            }
//            System.out.println("server Created!");
            wait(20000);
            Map<String, Object> serverData = waitingList.get(server.hashCode());
            Server entryServer = (Server) serverData.get("server");
            List<Client> clients = (List<Client>) serverData.get("clients");
            server.setState("active");
            server.setCapacity(entryServer.getCapacity());
            entryServer.addClient(client);
            server.setClients(entryServer.getClients());
            clientRepository.save(client);
            serverRepository.save(server);
            synchronized (waitingList) {
                waitingList.remove(server.hashCode());
            }
            return server;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Exception("server creation error: " + e.getMessage());
        }
    }


    @Override
    public void deleteServer(String id)  {
        serverRepository.deleteById(id);
    }

    @Override
    public void deleteAllServers()  {
        serverRepository.deleteAll();
    }
}
