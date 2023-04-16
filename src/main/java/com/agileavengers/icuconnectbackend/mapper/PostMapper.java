package com.agileavengers.icuconnectbackend.mapper;

import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.agileavengers.icuconnectbackend.model.Post;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.repository.CommentRepository;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = Collectors.class)

public abstract class PostMapper {
    MappingService mappingService;
    CommentRepository commentRepository;
    CommentMapper commentMapper;

    @Autowired
    public void setMappingService(MappingService mappingService, CommentRepository commentRepository, CommentMapper commentMapper) {
        this.mappingService = mappingService;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Mapping(target = "commentList", expression = "java(commentRepository.findAllByPost_Id(post.getId()).stream().map(commentMapper::toDto).collect(Collectors.toList()))")
    public abstract PostDto toDto(Post post);
    
    public abstract Post fromDto(PostDto postDto);
}
