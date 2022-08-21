package com.project.serverpool.repositories;

import com.project.serverpool.domain.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
}
