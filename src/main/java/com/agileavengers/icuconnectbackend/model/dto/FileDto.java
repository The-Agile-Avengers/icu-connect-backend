package com.agileavengers.icuconnectbackend.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FileDto {
    private Long id;

    private UserDto user;

    private Timestamp creation;

    private String filePath;

    private String fileName;
}
