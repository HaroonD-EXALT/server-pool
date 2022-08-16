package com.project.serverpool.model.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ClientDto {

    @NotNull
    @NotEmpty
    private String database;

    @Max(value = 100 , message = "Value should be less then equal to 100 GB")
    @Min(value = 1 , message = "Value should be greater then equal to 1 GB")
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
