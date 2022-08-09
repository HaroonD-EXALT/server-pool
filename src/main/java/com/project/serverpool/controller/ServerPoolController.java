package com.project.serverpool.controller;


import com.aerospike.client.AerospikeException;
import com.project.serverpool.model.Server;
import com.project.serverpool.model.ServerFactory;
import com.project.serverpool.model.ServerPool;
import com.project.serverpool.service.ServerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/server-pool")
public class ServerPoolController {
    @Autowired
    ServerPoolService   serverPoolService;

    @GetMapping
    public String hello(){
        return "hello from server";
    }

    @PostMapping("/create")
    public ResponseEntity<Object>   createNewServerPool(){
        try {
            ServerPool serverPool = serverPoolService.createNewServerPool();
            return ResponseEntity.status(HttpStatus.OK).body(serverPool);
        } catch (InterruptedException | AerospikeException | IllegalMonitorStateException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/locate")
    public ResponseEntity<Object>   locateNewServerToServerPool(@Valid @RequestBody Map<String, Object> serverData){
        try {
            Server server = ServerFactory.CreateServer((Integer) serverData.get("databaseID"), (Double) serverData.get("memory"));
            String result = serverPoolService.locateServerToPool(server);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (AerospikeException | IllegalMonitorStateException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
