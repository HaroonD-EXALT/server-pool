package com.project.serverpool.service;

import com.project.serverpool.database.AerospikeDatabase;
import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.model.dao.daoImpl.ClientDao;
import com.project.serverpool.model.dao.daoImpl.ServerDao;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServerService {

    private AerospikeDatabase database = AerospikeDatabase.getInstance();
    private ServerDao serverDao = new ServerDao();
    private ClientDao clientDao = new ClientDao();


    /*
        hash(server):{
                         "available capacity": 40,
                         "clients wait to locate a recourse":List<Client>
                     }
    */
    private Map<Integer, Map<String, Object>> waitingList = Collections.synchronizedMap(new HashMap<>());

    public List<Server> getAllServerPools() {

        return serverDao.getAll();
    }

    public Server locateServer(Client client) throws Exception {
        System.out.println("********** Hi **********");
        Server server;

        server = serverDao.findAvailableServer(client.getMemory());
        clientDao.save(client);

        System.out.println("-----------");
        System.out.println(server);
        System.out.println("-----------");

        //no available recourse in the current active servers
        //check the servers in the creation state
        if (server == null) {
            //loop over waiting list array to check if is there available resource in the creating state servers
            for (Map.Entry<Integer, Map<String, Object>> entry : waitingList.entrySet()) {
                int serverKey = entry.getKey();
                Map<String, Object> serverData = entry.getValue();
                //if the current server has available recourse
                if ((Long) serverData.get("avaCapacity") >= client.getMemory()) {
                    //ensure that only one thread is updating the list
                    long leftRecourse = (Long) serverData.get("avaCapacity") - client.getMemory();
                    List<Client> clients = (List<Client>) serverData.get("clients");
                    clients.add(client);
                    serverData.put("avaCapacity", leftRecourse);
                    serverData.put("clients", clients);
                    waitingList.put(serverKey, serverData);

                    return server;
                }
            }
            // current waiting list array do not have available resource in the creating state servers
            // spin new server
            server = createNewServer(client);


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
            server.config();

            // adding new server to waiting list
            waitingList.put(server.hashCode(), new HashMap() {{
                put("avaCapacity", server.getCapacity() - client.getMemory());
                put("clients", new ArrayList<Client>());
            }});
            wait(20000);
            Map<String, Object> serverData = waitingList.get(server.hashCode());
            long leftRecourse = (Long) serverData.get("avaCapacity");
            List<Client> clients = (List<Client>) serverData.get("clients");
            server.setState("active");
            server.setCapacity(leftRecourse);
            clients.add(client);
            server.setClients(clients);
            System.out.println("server " + server.getId() + " configured");
            System.out.println(server);
            clientDao.save(client);
            serverDao.update(server);
            waitingList.remove(server.hashCode());
            return server;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Exception("server creation error: "+e.getMessage());
        }
    }


    public boolean deleteServer(String id) throws Exception {
        return serverDao.delete(id);
    }

    public void deleteAllServers() throws Exception {
        serverDao.deleteAll();
    }
}
