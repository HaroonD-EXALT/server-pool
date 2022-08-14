package com.project.serverpool.model.dao.daoImpl;

import com.aerospike.client.Value;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.project.serverpool.database.AerospikeDatabase;
import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.model.dao.Dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerDao implements Dao<Server> {
    private AerospikeDatabase database = AerospikeDatabase.getInstance();


    // creating new server-pool
    @Override
    public Server save(Server item) throws InterruptedException {
//        item.setId(getNextId());
        database.getMapper().save(item);
        System.out.println("server created");
        return item;
    }

    @Override
    public Server getById(String id) throws Exception {
        return null;
    }

    @Override
    public List<Server> getAll() {
        List<Server> servers= database.getMapper().scan(Server.class);
        for (Server s:servers
             ) {
            System.out.println(s);
        }
        return  servers;
    }

    @Override
    public boolean update(Server item) {
        System.out.println(":---");
        System.out.println(item);
        database.getMapper().update(item);
        return true;
    }

    @Override
    public boolean delete(String id) throws Exception {
        return database.getMapper().delete(Server.class,id);
    }

    public synchronized Server findAvailableServer(double memory) {
        System.out.println("current thread : "+Thread.currentThread().toString());
//        if there is no server in database
        if (database.getMapper().scan(Server.class).size() == 0){
            return null;
        }
        Statement stmt = database.getStatement();
        stmt.setSetName("server");
        ResultSet rs = database.getAerospikeClient().queryAggregate(null, stmt,
                "get_available_pool", "available_pools",
                Value.get("active"), Value.get(memory));
        List<Server> serverList = new ArrayList<>();
        while (rs.next()) {
            Map<String,Object> result = (Map<String, Object>) rs.getObject();
            Server s = database.getMapper().read(Server.class,(String) result.get("PK"));
            serverList.add(s);
        }

        serverList.sort(this::compare);
        //return the best match server to locate the resource
        for (Server s:serverList
             ) {
            if(s.getCapacity() >= memory){
                return s;
            }
        }

        // spin new server
        return null;
    }

    private int compare(Server s1,Server s2){
        if (s1.getCapacity() > s2.getCapacity()){
            return 1;
        }else if (s1.getCapacity() < s2.getCapacity()){
            return 0;
        }else {
            return  -1;
        }
    }


    public void deleteAll() throws Exception {
        List<Server> servers = database.getMapper().scan(Server.class);
        for (Server s: servers
             ) {
            boolean rs =delete(s.getId());
        }
    }


//    private long getNextId() {
//        return database.getMapper().scan(Server.class).size() + 1;
//
//    }
}
