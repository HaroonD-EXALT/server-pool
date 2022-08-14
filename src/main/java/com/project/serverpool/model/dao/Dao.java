package com.project.serverpool.model.dao;


import java.util.List;

public interface Dao<T> {

    //creat
    T save(T item) throws InterruptedException;

    //read
    T getById(String id) throws Exception;

    List<T> getAll();

    //update
    boolean update( T item);

    //delete
    boolean delete(String id) throws Exception;


}
