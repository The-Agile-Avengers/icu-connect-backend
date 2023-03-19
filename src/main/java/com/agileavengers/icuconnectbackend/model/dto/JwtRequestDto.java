package com.agileavengers.icuconnectbackend.model.dto;

import java.io.Serializable;

public class JwtRequestDto implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String name;
    private String password;

    public JwtRequestDto() {
    }

    public JwtRequestDto(String name, String password) {
        this.setName(name);
        this.setPassword(password);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}