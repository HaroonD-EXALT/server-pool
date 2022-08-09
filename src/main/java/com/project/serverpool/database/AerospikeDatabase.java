package com.project.serverpool.database;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Language;
import com.aerospike.client.lua.LuaConfig;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.RegisterTask;
import com.aerospike.mapper.tools.AeroMapper;

public class AerospikeDatabase {
    private static AerospikeDatabase database;
    private static AerospikeClient aerospikeClient;
    private static WritePolicy writePolicy;

    private AeroMapper mapper ;
    private Statement stmt;

    String UDFDir = "./udf";
    String MAXID_UDFFile = "get_max_id_serverPool.lua";
    String FILTER_POOL_UDFFile = "get_available_pool.lua";
    private AerospikeDatabase() {
        aerospikeClient = new AerospikeClient("localhost", 3000);
        writePolicy = new WritePolicy();
        stmt = new Statement();
        stmt.setNamespace("test");
        mapper = new AeroMapper.Builder(aerospikeClient).build();
        RegisterTask task = aerospikeClient.register(new Policy(), UDFDir+"/"+ MAXID_UDFFile, MAXID_UDFFile, Language.LUA);
        task.waitTillComplete();
        RegisterTask task1 = aerospikeClient.register(new Policy(), UDFDir+"/"+ FILTER_POOL_UDFFile, FILTER_POOL_UDFFile, Language.LUA);
        task1.waitTillComplete();
        LuaConfig.SourceDirectory = "udf";


    }

    public static AerospikeDatabase getInstance() {
        if (aerospikeClient == null) {
            database = new AerospikeDatabase();
        }
        return database;
    }

    public  AerospikeClient getAerospikeClient() {
        return aerospikeClient;
    }

    public AeroMapper getMapper() {
        return mapper;
    }

    public Statement getStatement() {
        return stmt;
    }
}
