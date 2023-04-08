package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private UserDto creator;
    private Timestamp creation;
    
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String text;
    /**
     * Possible link to MongoDB
     */
    private String documentId;
    private List<CommentDto> commentList;
}
