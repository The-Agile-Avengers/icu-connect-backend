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

    /**
     * Score for teaching
     */
    private Double teaching;
    /**
     * Score for lecture content
     */
    private Double content;
    /**
     * score for needed workload
     */
    private Double workload;
    /**
     * user that created the rating
     */
    private UserDto user;
    /**
     * Time when the rating was created
     */
    private Timestamp creation;
    /**
     * optional text explaining the given scores or additional information
     */
    private String text;
    /**
     * How many users have liked the rating
     */
    private Integer thumbsUp;
    /**
     * if the currently logged in user has liked the rating
     */
    private Boolean hasLiked;
}
