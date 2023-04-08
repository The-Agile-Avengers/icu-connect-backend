package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * Get list of Posts that refer to a specific Community
     *
     * @param communityId Id of the relevant community
     * @return List of all referenced posts
     */
    Page<Post> findAllByCommunity_ModuleId(String moduleId, Pageable pageable);

    @Transactional()
    void deletePostById(Long id);
}
