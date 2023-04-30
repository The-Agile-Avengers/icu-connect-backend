package com.agileavengers.icuconnectbackend.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agileavengers.icuconnectbackend.model.Post;

import jakarta.transaction.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * Get list of Posts that refer to a specific Community
     *
     * @param moduleId Id of the relevant community
     * @return List of all referenced posts
     */
    Page<Post> findAllByCommunity_ModuleId(String moduleId, Pageable pageable);

    Page<Post> findAllByCreationBetween(Timestamp begin, Timestamp end, Pageable pageable);
    Optional<Post> findByIdAndCommunity_ModuleId(Long id, String moduleId);

    Boolean existsByCommunity_ModuleIdAndId(String moduleId, Long id);

    @Transactional()
    void deletePostById(Long id);

    @Transactional()
    void deletePostByIdAndCreator_Id(Long id, Long creatorId);
}
