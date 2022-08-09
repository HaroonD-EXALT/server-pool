package com.project.serverpool.service;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Value;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.project.serverpool.database.AerospikeDatabase;
import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerPool;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ServerPoolService {

    private AerospikeDatabase database = AerospikeDatabase.getInstance();

    public ServerPool createNewServerPool() throws InterruptedException {
        ServerPool serverPool = new ServerPool();
        serverPool.setId(getNextId());
        database.getMapper().save(serverPool);
        synchronized (this) {
                wait(10000);
                serverPool.configServerPool();
                database.getMapper().save(serverPool);
        }
        return serverPool;
    }



    public String locateServerToPool(Server server) {

        Statement stmt = database.getStatement();
        stmt.setSetName("server-pool");
//        stmt.setBinNames("state");
//        stmt.setFilter(Filter.equal("state","active"));
        // no filter for greater, filtering from server required memory to max memory
//        stmt.setFilter(Filter.range("capacity", (long) server.getMemory(),100));
//        RecordSet rs = database.getAerospikeClient().query(null,stmt);
        ResultSet rs = database.getAerospikeClient().queryAggregate(null, stmt, "get_available_pool", "get_available_pool", Value.get("active"),Value.get(server.getMemory()));
        while (rs.next()){
            System.out.print("---> ");
            System.out.println(rs.getObject());
        }


        return "";
    }



    private long getNextId() {
        Statement stmt = database.getStatement();
        stmt.setSetName("server-pool");
        ResultSet rs = database.getAerospikeClient().queryAggregate(null, stmt, "get_max_id_serverPool", "maxId");
        if (rs.next()) {
            System.out.println(rs);
            System.out.println(rs.getObject());
            Map<String, Long> result = (Map<String, Long>) rs.getObject();
            return result.get("PK") + 1;
        }

        throw new AerospikeException("Database error => error in finding next id");


    }
}
