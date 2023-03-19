package com.agileavengers.icuconnectbackend.model.dto;

import java.io.Serializable;

public class JwtResponseDto implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwt;

    public JwtResponseDto(String jwt) {
        super();
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
