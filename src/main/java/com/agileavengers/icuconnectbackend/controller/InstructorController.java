package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.service.IInstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for instructors.
 * Functionality will be extended, once instructors contain more information.
 */

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    IInstructorService instructorService;

    @Autowired
    public InstructorController(IInstructorService instructorService) {
        this.instructorService = instructorService;
    }

    /**
     * Queries all currently existing instructors
     * @return List of instructors.
     */
    @GetMapping(value = "")
    List<InstructorDto> getAllInstructors() {
        return instructorService.getAllInstructors();
    }
}
