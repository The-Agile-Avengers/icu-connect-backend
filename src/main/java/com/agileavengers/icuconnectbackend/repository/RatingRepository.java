package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Rating;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Transactional()
    void deleteRatingById(Long id);

    /**
     * Get list of Ratings that refer to a specific Community
     *
     * @param moduleId of the relevant community
     * @return List of all referenced ratings
     */
    Page<Rating> findAllByCommunity_ModuleId(String moduleId, Pageable pageable);

    List<Rating> findAllByCommunity_ModuleId(String communityId);

    Optional<Rating> findByCommunity_ModuleIdAndCreator_Id(String communityId, Long userId);

    Optional<Rating> findByIdAndCommunity_ModuleId(Long id, String moduleId);
}
