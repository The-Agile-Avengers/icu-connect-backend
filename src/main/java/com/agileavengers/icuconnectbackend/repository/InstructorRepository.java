package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Instructor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    @Transactional()
    void deleteInstructorById(Long id);

    Optional<Instructor> findInstructorByName(String name);
}
