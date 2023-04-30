package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)

public abstract class UserMapper {

    MappingService mappingService;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public void setMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public abstract UserDto toDto(User user);

    public abstract UserDetailDto toDetailedDto(User user);

    public abstract User fromDetailedDto(UserDetailDto user);

    @Mapping(target = "password", qualifiedByName = "encrypt")
    public abstract User fromDto(RegisterUserDto registerUserDto);

    @Named("encrypt")
    String encrypt(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
