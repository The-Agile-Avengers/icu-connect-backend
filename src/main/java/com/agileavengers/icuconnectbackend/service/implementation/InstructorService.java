package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.service.IInstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorService implements IInstructorService {

    private final InstructorMapper instructorMapper;
    InstructorRepository instructorRepository;

    @Autowired
    public InstructorService(InstructorMapper instructorMapper, InstructorRepository instructorRepository) {
        this.instructorMapper = instructorMapper;
        this.instructorRepository = instructorRepository;
    }

    @Override
    public List<InstructorDto> getAllInstructors() {
        return instructorRepository.findAll().stream().map(instructorMapper::toDto).collect(Collectors.toList());
    }
}
