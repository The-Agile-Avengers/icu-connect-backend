package com.agileavengers.icuconnectbackend.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.agileavengers.icuconnectbackend.model.Comment;
import com.agileavengers.icuconnectbackend.model.dto.CommentDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CommentMapper {
    MappingService mappingService;

    @Autowired
    public void setMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Mapping(target = "user", source = "creator")
    public abstract CommentDto toDto(Comment comment);

    @Mapping(target = "post", expression = "java(null)")
    @Mapping(target = "creator", source = "user")
    public abstract Comment fromDto(CommentDto commentDto);
}
