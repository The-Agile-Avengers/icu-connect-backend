package com.agileavengers.icuconnectbackend.model.dto;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private UserDto user;
    private Timestamp creation;

    /**
     * Title of the post
     */
    @NotBlank(message = "Title cannot be blank")
    private String title;
    /**
     * Content of the post
     */
    @NotBlank(message = "Text cannot be blank")
    private String text;
    /**
     * Possible link to MongoDB
     */
    private String documentId;
    /**
     * Comments made concerning the post
     */
    private List<CommentDto> commentList;
}
