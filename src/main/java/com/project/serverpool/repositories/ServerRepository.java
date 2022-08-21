package com.project.serverpool.repositories;

import com.project.serverpool.domain.Server;
import org.springframework.data.repository.CrudRepository;

public interface ServerRepository extends CrudRepository<Server, String> {
    Server  findByCapacityIsGreaterThanEqualAndStateEqualsOrderByCapacityAsc(Long capacity,String state);
}

