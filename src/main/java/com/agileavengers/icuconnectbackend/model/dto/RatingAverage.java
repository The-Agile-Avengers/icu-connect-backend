package com.agileavengers.icuconnectbackend.model.dto;

import com.agileavengers.icuconnectbackend.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Contains the calculated average rating scores for a community.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingAverage {

    private Long id;

    /**
     * Calculated average teaching score
     */
    private Double teaching;
    /**
     * Calculated average content score
     */
    private Double content;
    /**
     * Calculated average workload score
     */
    private Double workload;

    /**
     * Method to create a new average object based on a list of ratings
     * @param ratings list of relevant ratings
     */
    public RatingAverage(List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return;
        }
        this.teaching = ratings.stream().mapToDouble(Rating::getTeaching).sum() / ratings.size();
        this.content = ratings.stream().mapToDouble(Rating::getContent).sum() / ratings.size();
        this.workload = ratings.stream().mapToDouble(Rating::getWorkload).sum() / ratings.size();
    }
}
