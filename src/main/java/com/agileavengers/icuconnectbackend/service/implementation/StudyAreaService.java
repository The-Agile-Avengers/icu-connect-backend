package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.StudyAreaMapper;
import com.agileavengers.icuconnectbackend.model.StudyArea;
import com.agileavengers.icuconnectbackend.model.dto.StudyAreaDto;
import com.agileavengers.icuconnectbackend.repository.StudyAreaRepository;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudyAreaService implements IStudyAreaService {

    private final StudyAreaMapper studyAreaMapper;
    StudyAreaRepository studyAreaRepository;

    @Autowired
    public StudyAreaService(StudyAreaMapper studyAreaMapper, StudyAreaRepository studyAreaRepository) {
        this.studyAreaMapper = studyAreaMapper;
        this.studyAreaRepository = studyAreaRepository;
    }

    @Override
    public List<StudyAreaDto> getStudyAreas() {
        return studyAreaRepository.findAll().stream().map(studyAreaMapper::toDto).toList();
    }

    public StudyArea getOrCreateStudyArea(StudyArea studyArea) {
        Optional<StudyArea> optionalStudyArea = studyAreaRepository.findStudyAreaByName(studyArea.getName());
        return optionalStudyArea.orElseGet(() -> studyAreaRepository.save(studyArea));

    }
}
