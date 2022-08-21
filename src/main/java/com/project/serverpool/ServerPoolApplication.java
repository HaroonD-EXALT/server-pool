package com.project.serverpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
public class ServerPoolApplication {



    public static void main(String[] args) {
        SpringApplication.run(ServerPoolApplication.class, args);
    }



}
