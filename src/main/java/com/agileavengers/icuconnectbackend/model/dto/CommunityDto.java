package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDto {
    private String moduleId;

    private String name;

    private InstructorDto instructor;

    /**
     * How many users are subscribed to
     */
    private Integer subscribersCount;

    /**
     * Average rating object
     */
    private RatingAverage rating;

    private Boolean joined;

}
