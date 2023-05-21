package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.StudyArea;
import com.agileavengers.icuconnectbackend.model.dto.StudyAreaDto;

import java.util.List;

public interface IStudyAreaService {
    List<StudyAreaDto> getStudyAreas();
    StudyArea getOrCreateStudyArea(StudyArea studyArea);

}
