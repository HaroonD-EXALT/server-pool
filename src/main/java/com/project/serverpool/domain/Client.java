package com.project.serverpool.domain;

import com.aerospike.mapper.annotations.AerospikeBin;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import lombok.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.aerospike.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;


@Data
@Document
@AllArgsConstructor
public class Client {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String database;


    @NotNull
    @NotEmpty
    private long memory ;

    private String privateData ;
    private String anotherData ;
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
