package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    private Long id;

    private Double teaching;
    private Double content;
    private Double workload;

    private UserDto user;
    private Timestamp creation;

    private String text;

    private Integer thumbsUp;

    private Boolean hasLiked;
}
