package com.agileavengers.icuconnectbackend.model.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class JwtResponseDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwt;
    private final String avatar;
    private final String username;

    public JwtResponseDto(String jwt, String username, String avatar) {
        super();
        this.jwt = jwt;
        this.username = username;
        this.avatar = avatar;
    }
}
