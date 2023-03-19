package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}
