package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private UserDto creator;
    private Timestamp creation;
    private String title;
    private String text;
    /**
     * Possible link to MongoDB
     */
    private String documentId;
    private List<CommentDto> commentList;
}
