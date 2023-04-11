package com.agileavengers.icuconnectbackend.model.dto;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    private UserDto creator;

    private Timestamp creation;

    @NotBlank(message = "Text field should not be null")
    private String text;
}
