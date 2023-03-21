package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Comment;
import com.agileavengers.icuconnectbackend.model.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring"
)
public abstract class CommentMapper {

    public abstract CommentDto toDto(Comment comment);

    @Mapping(target = "post", expression = "java(null)")
    public abstract Comment fromDto(CommentDto commentDto);
}
