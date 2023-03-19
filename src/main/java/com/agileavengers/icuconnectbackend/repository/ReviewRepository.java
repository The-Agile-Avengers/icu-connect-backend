package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Get list of Reviews that refer to a specific Community
     * @param communityId Id of the relevant community
     * @return List of all referenced reviews
     */
    List<Review> findAllByCommunity_Id(Long communityId);
}
