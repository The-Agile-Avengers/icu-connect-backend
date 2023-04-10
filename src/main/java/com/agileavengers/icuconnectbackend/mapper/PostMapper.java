package com.agileavengers.icuconnectbackend.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.agileavengers.icuconnectbackend.model.Post;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)

public abstract class PostMapper {
    MappingService mappingService;

    @Autowired
    public void setMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public abstract PostDto toDto(Post post);

    public abstract Post fromDto(PostDto postDto);
}
