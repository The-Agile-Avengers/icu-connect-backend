package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    private Long id;

    private CommunityDto community;

    private Double teaching;
    private Double content;
    private Double workload;


    private String text;

    private Integer thumbsUp;
}
