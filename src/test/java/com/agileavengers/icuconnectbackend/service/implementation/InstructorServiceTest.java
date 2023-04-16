package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.service.IInstructorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructorServiceTest {


    IInstructorService instructorService;

    @Mock
    InstructorRepository instructorRepository;

    @BeforeEach
    void setup() {
        instructorRepository = mock(InstructorRepository.class);
        InstructorMapper instructorMapper = Mappers.getMapper(InstructorMapper.class);
        this.instructorService = new InstructorService(instructorMapper, instructorRepository);
    }

    @Test
    void getAllInstructors() {
        Instructor instructor1 = Instructor.builder().id(1L).name("Max Muster").build();
        Instructor instructor2 = Instructor.builder().id(1L).name("Eva Müller").build();
        when(instructorRepository.findAll()).thenReturn(List.of(instructor1, instructor2));

        List<InstructorDto> result = instructorService.getAllInstructors();
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
