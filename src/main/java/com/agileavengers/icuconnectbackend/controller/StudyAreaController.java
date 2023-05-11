package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.StudyAreaDto;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/studyareas")
public class StudyAreaController {

    IStudyAreaService studyAreaService;

    @Autowired
    public StudyAreaController(IStudyAreaService studyAreaService) {
        this.studyAreaService = studyAreaService;
    }

    @GetMapping(value = "")
    List<StudyAreaDto> getAllStudyAreas() {
        return studyAreaService.getStudyAreas();
    }
}
