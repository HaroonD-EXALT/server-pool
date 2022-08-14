package com.project.serverpool.controller;


import com.aerospike.client.AerospikeException;
import com.project.serverpool.domain.Client;
import com.project.serverpool.domain.Server;
import com.project.serverpool.model.dto.ClientDto;
import com.project.serverpool.model.dto.ServerDto;
import com.project.serverpool.service.ServerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
            final Server[] server = new Server[1];
            Thread t =new Thread(() -> {
                Client client;
               synchronized (this){
                   System.out.println("Client Dto: "+ clientDto);
                   client = mapper.map(clientDto, Client.class);
                   System.out.println("Client : "+ client);
               }

                try {
                     server[0] = serverService.locateServer(client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
            //wait until thread finish execution
            t.join();

            return ResponseEntity.status(HttpStatus.OK).body(mapper.map(server[0],ServerDto.class));





        } catch (AerospikeException | IllegalMonitorStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
//            e.printStackTrace();
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
