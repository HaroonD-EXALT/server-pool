package com.project.serverpool.model;

import com.aerospike.mapper.annotations.AerospikeBin;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@Component
@AerospikeRecord(namespace="test", set="server")
@ComponentScan
public abstract class Server {
    @AerospikeKey
    @AerospikeBin(name = "PK")
    private long id;

    @AerospikeBin
    @NotNull
    @NotEmpty
    private int database;



    @AerospikeBin
    @NotNull
    @NotEmpty
    @Max(value = 100 , message = "Value should be less then equal to 100 GB")
    @Min(value = 1 , message = "Value should be greater then equal to 1 GB")
    private double memory;

    public abstract String configDatabase();

    public String dataBaseName(){
        switch (database){
            case 1:
                return "MYSQL";
            case 2:
                return "ORACLE";
            case 3:
                return "MONGODB";
            case 4:
                return "MARIADB";
            default:
                return "unknown DB";
        }
    }


    @Override
    public String toString() {
        return "Server{" +
                "capacity=" + memory +
                ", database=" + dataBaseName() +
                '}';
    }


}
