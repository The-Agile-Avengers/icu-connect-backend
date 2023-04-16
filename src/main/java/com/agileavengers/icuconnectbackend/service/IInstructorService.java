package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;

import java.util.List;

public interface IInstructorService {
    List<InstructorDto> getAllInstructors();
}
