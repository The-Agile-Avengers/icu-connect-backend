package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Instructor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstructorRepository extends PagingAndSortingRepository<Instructor, Long> {
}
