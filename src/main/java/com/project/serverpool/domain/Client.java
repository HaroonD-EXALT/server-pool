package com.project.serverpool.domain;

import com.aerospike.mapper.annotations.AerospikeBin;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Component
@Getter
@Setter
@AerospikeRecord(namespace="test", set="client")
public class Client {

    @AerospikeKey
    @AerospikeBin(name = "PK")
    private String id;

    @AerospikeBin
    @NotNull
    @NotEmpty
    private String database;


    @AerospikeBin
    @NotNull
    @NotEmpty
    private int memory ;

    @AerospikeBin
    private String privateData ;
    @AerospikeBin
    private String anotherData ;
    @AerospikeBin
    private String something ;

    public Client() {
        this.id = UUID.randomUUID().toString();
        this.privateData = "privateData";
        this.anotherData = "anotherData";
        this.something = "something";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id && memory == client.memory && database.equals(client.database);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, database, memory);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", database='" + database + '\'' +
                ", memory=" + memory +
                '}';
    }
}
