package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.StudyAreaMapper;
import com.agileavengers.icuconnectbackend.model.StudyArea;
import com.agileavengers.icuconnectbackend.model.dto.StudyAreaDto;
import com.agileavengers.icuconnectbackend.repository.StudyAreaRepository;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudyAreaServiceTest {

    IStudyAreaService studyAreaService;

    @Mock
    StudyAreaRepository studyAreaRepository;

    @BeforeEach
    void setup() {
        studyAreaRepository = mock(StudyAreaRepository.class);
        StudyAreaMapper studyAreaMapper = Mappers.getMapper(StudyAreaMapper.class);
        this.studyAreaService = new StudyAreaService(studyAreaMapper, studyAreaRepository);

    }

    @Test
    void getStudyAreas() {
        StudyArea studyarea1 = StudyArea.builder().name("Study Area 1").build();
        StudyArea studyarea2 = StudyArea.builder().name("Study Area 2").build();

        when(studyAreaRepository.findAll()).thenReturn(List.of(studyarea1, studyarea2));
        List<StudyAreaDto> result = studyAreaService.getStudyAreas();
        verify(studyAreaRepository, times(1)).findAll();
        assertNotNull(result, "Result should not be empty.");
        assertEquals(2, result.size(), "Result should contain exactly two elements");

    }

    @Test
    void getOrCreateStudyArea() {
        StudyArea studyarea = StudyArea.builder().name("Study Area").build();

        when(studyAreaRepository.findStudyAreaByName("Study Area")).thenReturn(Optional.of(studyarea));

        StudyArea result = studyAreaService.getOrCreateStudyArea(studyarea);
        verify(studyAreaRepository, times(1)).findStudyAreaByName("Study Area");
        assertNotNull(result, "Result should not be empty.");
        assertEquals(studyarea.getName(), result.getName(), "Result should have the same name");
        assertNull(result.getId(), "Result should not have an ID set, since the mock returns the studyarea as it is");
    }

    @Test
    void getOrCreateStudyAreaNew() {
        StudyArea studyarea = StudyArea.builder().name("Study Area").build();

        when(studyAreaRepository.findStudyAreaByName("Study Area")).thenReturn(Optional.empty());

        when(studyAreaRepository.save(Mockito.any(StudyArea.class)))
                .thenAnswer(i -> {
                    StudyArea argument = (StudyArea) i.getArguments()[0];
                    argument.setId(1L);
                    return argument;
                });

        StudyArea result = studyAreaService.getOrCreateStudyArea(studyarea);
        verify(studyAreaRepository, times(1)).findStudyAreaByName("Study Area");
        assertNotNull(result, "Result should not be empty.");
        assertEquals(studyarea.getName(), result.getName(), "Result should have the same name");
        assertEquals(1L,result.getId(), "Result should have an ID set, since the save method sets an ID.");
    }
}
