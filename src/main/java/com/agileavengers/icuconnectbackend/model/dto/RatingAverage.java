package com.agileavengers.icuconnectbackend.model.dto;

import com.agileavengers.icuconnectbackend.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingAverage {

    private Long id;

    private Double teaching;
    private Double content;
    private Double workload;

    public RatingAverage(List<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return;
        }
        this.teaching = ratings.stream().mapToDouble(Rating::getTeaching).sum() / ratings.size();
        this.content = ratings.stream().mapToDouble(Rating::getContent).sum() / ratings.size();
        this.workload = ratings.stream().mapToDouble(Rating::getWorkload).sum() / ratings.size();
    }
}
