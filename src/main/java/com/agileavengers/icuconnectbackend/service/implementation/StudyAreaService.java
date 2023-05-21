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

    /**
     * Get a list of all currently defined study areas
     * @return list of study areas
     */
    @Override
    public List<StudyAreaDto> getStudyAreas() {
        return studyAreaRepository.findAll().stream().map(studyAreaMapper::toDto).toList();
    }

    /**
     * Get study area object from database if it exists. Else create and persist a new object.
     * @param studyArea object containing name that should be looked for
     * @return retrieved or created object
     */
    @Override
    public StudyArea getOrCreateStudyArea(StudyArea studyArea) {
        Optional<StudyArea> optionalStudyArea = studyAreaRepository.findStudyAreaByName(studyArea.getName());
        return optionalStudyArea.orElseGet(() -> studyAreaRepository.save(studyArea));

    }
}
