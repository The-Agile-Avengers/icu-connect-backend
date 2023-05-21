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
    /**
     * unique module id, used as main reference
     */
    private String moduleId;

    /**
     * Name of the community
     */
    private String name;

    /**
     * Instructor teaching the course
     */
    private InstructorDto instructor;

    /**
     * How many users are subscribed to
     */
    private Integer subscribersCount;

    /**
     * Number of ects the course has
     */
    private Double ects;

    /**
     * Average rating object
     */
    private RatingAverage rating;

    /**
     * Whether the logged in user has subscribed the course or not
     */
    private Boolean joined;

}
