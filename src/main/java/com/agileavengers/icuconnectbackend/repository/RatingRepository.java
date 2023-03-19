package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Rating;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Transactional()
    void deleteRatingById(Long id);
}
