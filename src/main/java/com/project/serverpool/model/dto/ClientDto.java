package com.project.serverpool.model.dto;

public class ClientDto {


    private String database;
    private int memory ;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "database='" + database + '\'' +
                ", memory=" + memory +
                '}';
    }
}
