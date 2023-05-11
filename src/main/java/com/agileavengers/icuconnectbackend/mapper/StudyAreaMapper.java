package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.StudyArea;
import com.agileavengers.icuconnectbackend.model.dto.StudyAreaDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StudyAreaMapper {
    StudyAreaDto toDto(StudyArea studyArea);

    StudyArea fromDto(StudyAreaDto studyAreaDto);
}
