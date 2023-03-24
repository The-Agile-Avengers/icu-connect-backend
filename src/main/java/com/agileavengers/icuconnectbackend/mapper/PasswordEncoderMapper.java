package com.agileavengers.icuconnectbackend.mapper;

import org.mapstruct.Named;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordEncoderMapper {

    final PasswordEncoder passwordEncoder;

    @EncodedMapper
    @Named("ENCODED")
    public String encode(String value) {
        return passwordEncoder.encode(value);
    }
}
