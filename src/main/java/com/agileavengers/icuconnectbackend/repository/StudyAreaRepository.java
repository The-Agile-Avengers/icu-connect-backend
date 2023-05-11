package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.StudyArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyAreaRepository extends JpaRepository<StudyArea, Long> {

    Optional<StudyArea> findStudyAreaByName(String name);
}
