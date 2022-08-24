package com.project.serverpool.repositories;

import com.project.serverpool.domain.Server;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ServerRepository extends CrudRepository<Server, String> {
    List<Server> findByCapacityIsGreaterThanEqualAndStateEqualsOrderByCapacityAsc(Long capacity, String state);
}

