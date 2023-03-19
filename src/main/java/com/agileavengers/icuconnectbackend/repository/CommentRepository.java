package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Comment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
}
