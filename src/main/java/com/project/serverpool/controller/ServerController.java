package com.project.serverpool.controller;


import com.aerospike.client.AerospikeException;
import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.model.dto.ClientDto;
import com.project.serverpool.model.dto.ServerDto;
import com.project.serverpool.service.ServerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/server-pool")
public class ServerController {
    ServerService serverService = new ServerService();

    private ModelMapper mapper = new ModelMapper();


    @GetMapping
    public ResponseEntity getAllServer() {
        List<Server> serverPoolList = serverService.getAllServerPools();
        return ResponseEntity.status(HttpStatus.OK).body(serverPoolList.stream().map(server -> mapper.map(server, ServerDto.class)));
    }

    @PostMapping("/locate")
    public ResponseEntity<Object> locateNewServerToServerPool(@Valid @RequestBody ClientDto clientDto) {
        try {
            Server server;
            Client client;
            client = mapper.map(clientDto, Client.class);
            System.out.println(client);
            try {
                server = serverService.locateServer(client);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return ResponseEntity.status(HttpStatus.OK).body(mapper.map(server, ServerDto.class));


        } catch (AerospikeException | IllegalMonitorStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteServer(@PathVariable String id) {
        try {
            boolean serverPool = serverService.deleteServer(id);
            return ResponseEntity.status(HttpStatus.OK).body(serverPool);
        } catch (AerospikeException | IllegalMonitorStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAllServer() {
        try {
            serverService.deleteAllServers();
            return ResponseEntity.status(HttpStatus.OK).body("done");
        } catch (AerospikeException | IllegalMonitorStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
