package com.example.database_cli.model.entity;

import lombok.Data;

@Data
public class Admin {
    String id;
    String name;
    String password;
    boolean isBool=true;
}
