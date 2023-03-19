package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
