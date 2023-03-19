package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional()
    void deleteCommentById(Long id);
}
