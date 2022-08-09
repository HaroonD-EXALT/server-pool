package com.project.serverpool.model;

import com.project.serverpool.model.servers.*;

public class ServerFactory {
    public static final int MYSQL = 1;
    public static final int ORACLE = 2;
    public static final int MONGODB = 3;
    public static final int MARIADB = 4;
    public static final int AEROSPIKE = 5;

    public static Server CreateServer(int dataBaseID,double memory) {
        switch (dataBaseID){
            case MYSQL :
                return new MySqlServer(memory);
            case ORACLE :
                return new OracleServer(memory);
            case MONGODB :
                return new MongoDBServer(memory);
            case MARIADB :
                return new MariaDBServer(memory);
            case AEROSPIKE :
                return new AerospikeServer(memory);
            default:
                return null;
        }

    }
}
