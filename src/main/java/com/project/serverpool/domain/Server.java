package com.project.serverpool.domain;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AerospikeRecord(namespace="test", set="server")
@ComponentScan
public class Server {
    @AerospikeKey
    @AerospikeBin(name = "PK")
    private String id;



    @AerospikeBin
    private String state;

    @AerospikeBin
    @NotNull
    @NotEmpty
    @Max(value = 100 , message = "Value should be less then equal to 100 GB")
    @Min(value = 1 , message = "Value should be greater then equal to 1 GB")
    private long capacity ;

    @AerospikeBin
    private List<Client> clients;

    public Server() {
        this.id = UUID.randomUUID().toString();
        this.capacity=100;
        this.state="creating";
        this.clients = new ArrayList<>();
    }

    public  void config(){
        System.out.println("server "+this.id+" is in configuring");
    };


    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", capacity=" + capacity +
                ", clients=" + clients +
                '}';
    }

    public void addClient(Client client){
        this.clients.add(client);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return id == server.id && capacity == server.capacity &&  state.equals(server.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
