package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * Get list of Posts that refer to a specific Community
     * @param communityId Id of the relevant community
     * @return List of all referenced posts
     */
    List<Post> findAllByCommunity_Id(Long communityId);
}
