package com.agileavengers.icuconnectbackend.mapper;

import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.agileavengers.icuconnectbackend.model.File;
import com.agileavengers.icuconnectbackend.model.dto.FileDto;
import com.agileavengers.icuconnectbackend.repository.CommentRepository;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = Collectors.class)

public abstract class FileMapper {
    MappingService mappingService;
    CommentRepository commentRepository;
    CommentMapper commentMapper;

    @Autowired
    public void setMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Mapping(target = "user", source = "creator")
    public abstract FileDto toDto(File file);

    @Mapping(target = "creator", source = "user")
    public abstract File fromDto(FileDto fileDto);
}
