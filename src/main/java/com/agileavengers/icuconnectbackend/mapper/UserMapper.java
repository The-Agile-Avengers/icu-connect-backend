package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring"
)
public abstract class UserMapper {

    public abstract UserDto toDto(User user);
    public abstract User fromDto(UserDto userDto);
}
