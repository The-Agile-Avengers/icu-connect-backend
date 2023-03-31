package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Community;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Transactional()
    Long deleteCommunityById(Long id);

    Optional<Community> findCommunityByModuleId(String moduleId);
    Optional<Community> findCommunityById(Long communityId);
}
