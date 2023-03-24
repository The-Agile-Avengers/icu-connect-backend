package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InstructorMapper {
    InstructorDto toDto(Instructor instructor);
    Instructor fromDto(InstructorDto instructorDto);
}
