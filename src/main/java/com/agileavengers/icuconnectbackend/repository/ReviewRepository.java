package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Get list of Reviews that refer to a specific Community
     * @param communityId Id of the relevant community
     * @return List of all referenced reviews
     */
    Page<Review> findAllByCommunity_Id(Long communityId, Pageable pageable);
    @Transactional()
    void deleteReviewById(Long id);
}
