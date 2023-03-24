package com.agileavengers.icuconnectbackend.model.dto;

import com.agileavengers.icuconnectbackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    private Long id;

    private CommunityDto communityDto;

    private Double teaching;
    private Double content;
    private Double workload;


    private String text;

    private Integer thumbsUp;
}
