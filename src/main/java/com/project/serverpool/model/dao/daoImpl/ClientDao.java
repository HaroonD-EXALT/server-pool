package com.project.serverpool.model.dao.daoImpl;

import com.project.serverpool.database.AerospikeDatabase;
import com.project.serverpool.domain.Client;
import com.project.serverpool.model.dao.Dao;

import java.util.List;

public class ClientDao implements Dao<Client> {

    private final AerospikeDatabase database = AerospikeDatabase.getInstance();
    @Override
    public Client save(Client item) throws InterruptedException {
        database.getMapper().save(item);
        return item;
    }

    @Override
    public Client getById(String id) throws Exception {
        return null;
    }

    @Override
    public List<Client> getAll() {
        return null;
    }

    @Override
    public boolean update(Client item) {
        return false;
    }

    @Override
    public boolean delete(String id) throws Exception {
        return false;
    }
}
