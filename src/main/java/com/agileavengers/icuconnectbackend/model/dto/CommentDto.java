package com.agileavengers.icuconnectbackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    /**
     * The user that created the comment
     */
    private UserDto user;

    /**
     * Time when the comment was created
     */
    private Timestamp creation;

    /**
     * Content of the comment
     */
    @NotBlank(message = "Text field should not be null")
    private String text;
}
