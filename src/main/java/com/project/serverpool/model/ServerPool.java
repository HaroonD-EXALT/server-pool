package com.project.serverpool.model;

import com.aerospike.mapper.annotations.AerospikeBin;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AerospikeRecord(namespace="test", set="server-pool")
@ComponentScan
public class ServerPool {

    @AerospikeKey
    @AerospikeBin(name = "PK")
    @NotNull
    @NotEmpty
    private long id;



    @AerospikeBin
    @NotNull
    @NotEmpty
    private double capacity ;

    @AerospikeBin
    @NotNull
    @NotEmpty
    private String state;

    private List<Server> serverList;

    public ServerPool() {
        this.state = "creating";
        this.capacity = 100;
        this.serverList = new ArrayList<>();
    }

    public void configServerPool(){
        this.setState("active" );
    }
    public void addToServerPool(Server server) throws Exception{
        if (server.getMemory() > this.getCapacity()){
            throw new Exception("server cant not be added to this pool!");
        }

        if (server.getMemory() <= 0 || server.getMemory() >100){
            throw new Exception("un-allowed memory!");
        }

        this.setCapacity(this.getCapacity() - server.getMemory());;
        this.getServerList().add(server);

    }

    @Override
    public String toString() {
        return "ServerPool{" +
                "id=" + getId() +
                ", capacity=" + getCapacity() +
                ", state='" + getState() + '\'' +
                ", serverList=" + getServerList() +
                '}';
    }
}
