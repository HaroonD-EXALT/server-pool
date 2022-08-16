package com.project.serverpool.service;

import com.project.serverpool.database.AerospikeDatabase;
import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.model.dao.daoImpl.ClientDao;
import com.project.serverpool.model.dao.daoImpl.ServerDao;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServerService {

    private final ServerDao serverDao = new ServerDao();
    private final ClientDao clientDao = new ClientDao();

    /*
        hash(server):{
                         "server": Server,
                         "available capacity": 40,
                         "clients wait to locate a recourse":List<Client>
                     }
    */
    private Map<Integer, Map<String, Object>> waitingList = new HashMap<>();



    public List<Server> getAllServerPools() {
        return serverDao.getAll();
    }

    public  Server locateServer(Client client) throws Exception {
        Server server = new Server();
        boolean isRecourseLocatedInCreatingSate = false;
        synchronized (server){
            server = serverDao.findAvailableServer(client.getMemory());
        }
        clientDao.save(client);

        //no available recourse in the current active servers
        //check the servers in the creation state
        if (server == null) {
            //loop over waiting list array to check if is there available resource in the creating state servers
            for (Map.Entry<Integer, Map<String, Object>> entry : waitingList.entrySet()) {
                //ensure that only one thread is read/write the list
                //to read the most recent data from the list
                synchronized (waitingList){
                    int serverKey = entry.getKey();
                    Map<String, Object> serverData = entry.getValue();
                    Server entryServer = (Server) serverData.get("server");
                    //if the current server has available recourse
                    if ((Long) serverData.get("avaCapacity") >= client.getMemory()) {
                        long leftRecourse = (Long) serverData.get("avaCapacity") - client.getMemory();
                        List<Client> clients = (List<Client>) serverData.get("clients");
                        clients.add(client);
                        entryServer.setCapacity(leftRecourse);
                        entryServer.setClients(clients);
                        serverData.put("avaCapacity", leftRecourse);
                        serverData.put("clients", clients);
                        serverData.put("server", entryServer);
                        waitingList.put(serverKey, serverData);
                        // #TODO:solve return issue
                        //server in the creating state
                        isRecourseLocatedInCreatingSate = true;
                        server =entryServer;
                    }
                }
            }
            // current waiting list array do not have available resource in the creating state servers
            // spin new server
            if (isRecourseLocatedInCreatingSate){
                //wait until finish creating
                while (waitingList.get(server.hashCode()) != null){

                }
                return server;

            }
            else {
                server = createNewServer(client);
            }

        } else {
            //there is available recourse in the current active servers
            clientDao.save(client);
            server.setCapacity(server.getCapacity() - client.getMemory());
            server.addClient(client);
            serverDao.update(server);

        }
        return server;

    }


    public synchronized Server createNewServer(Client client) throws Exception {
        try {

            Server server = serverDao.save(new Server());

            // adding new server to waiting list
            synchronized (waitingList) {
                waitingList.put(server.hashCode(), new HashMap() {{
                    put("server", server);
                    put("avaCapacity", server.getCapacity() - client.getMemory());
                    put("clients", new ArrayList<Client>());
                }});
            }
            wait(20000);
            Map<String, Object> serverData = waitingList.get(server.hashCode());
            long leftRecourse = (Long) serverData.get("avaCapacity");
            List<Client> clients = (List<Client>) serverData.get("clients");
            server.setState("active");
            server.setCapacity(leftRecourse);
            clients.add(client);
            server.setClients(clients);
            clientDao.save(client);
            serverDao.update(server);
            synchronized (waitingList){
                waitingList.remove(server.hashCode());
            }
            return server;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Exception("server creation error: " + e.getMessage());
        }
    }


    public boolean deleteServer(String id) throws Exception {
        return serverDao.delete(id);
    }

    public void deleteAllServers() throws Exception {
        serverDao.deleteAll();
    }
}
