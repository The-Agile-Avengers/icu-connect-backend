package com.agileavengers.icuconnectbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RegisterUserDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDto;

@Mapper(componentModel = "spring", uses = EncodedMapper.class)
public abstract class UserMapper {

    public abstract UserDto toDto(User user);

    public abstract User fromDto(UserDto userDto);

    // @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapper.class)
    public abstract User fromDto(RegisterUserDto userDto);
}
