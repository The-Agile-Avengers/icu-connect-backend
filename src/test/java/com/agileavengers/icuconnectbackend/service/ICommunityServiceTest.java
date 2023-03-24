package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.mapper.*;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.repository.*;
import com.agileavengers.icuconnectbackend.service.implementation.CommunityService;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CommunityMapperImpl.class, ReviewMapperImpl.class, RatingMapperImpl.class, InstructorMapperImpl.class, MappingService.class})
class ICommunityServiceTest {

    ICommunityService communityService;

    @MockBean
    CommunityRepository communityRepository;
    @MockBean
    InstructorRepository instructorRepository;
    @MockBean
    ReviewRepository reviewRepository;
    @MockBean
    RatingRepository ratingRepository;
    @MockBean
    UserRepository userRepository;

//    private CommunityMapper communityMapper
//            = Mappers.getMapper(CommunityMapper.class);
//    private ReviewMapper reviewMapper
//            = Mappers.getMapper(ReviewMapper.class);
//    private RatingMapper ratingMapper
//            = Mappers.getMapper(RatingMapper.class);
//    private InstructorMapper instructorMapper
//            = Mappers.getMapper(InstructorMapper.class);

    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private RatingMapper ratingMapper;
    @Autowired
    private InstructorMapper instructorMapper;

    ICommunityServiceTest() {
        communityRepository = mock(CommunityRepository.class);
        instructorRepository = mock(InstructorRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        communityRepository = mock(CommunityRepository.class);
        userRepository = mock(UserRepository.class);
        this.communityService = new CommunityService(communityRepository, instructorRepository,
                reviewRepository, ratingRepository, userRepository,
                this.communityMapper, this.reviewMapper, this.ratingMapper, this.instructorMapper
        );
    }

    @Test
    void setupExampleCommunity() {
    }

    @Test
    void createCommunityNewInstructor() {
//        communityRepository = mock(CommunityRepository.class);
//        instructorRepository = mock(InstructorRepository.class);
//        reviewRepository = mock(ReviewRepository.class);
//        communityRepository = mock(CommunityRepository.class);
//        userRepository = mock(UserRepository.class);

        when(instructorRepository.save(Mockito.any(Instructor.class)))
                .thenAnswer(i -> {
                    Instructor argument = (Instructor) i.getArguments()[0];
                    argument.setId(2L);
                    return argument;
                });
        when(communityRepository.save(Mockito.any(Community.class)))
                .thenAnswer(i -> {
                    Instructor argument = (Instructor) i.getArguments()[0];
                    argument.setId(1L);
                    return argument;
                });
        InstructorDto instructor = InstructorDto.builder().name("Test Instructor").build();
        CommunityDto communityDto = CommunityDto.builder().name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        CommunityDto output = communityService.createCommunity(communityDto);
        verify(communityRepository, times(1)).save(argThat(
                x -> true ));
        verify(communityRepository, times(1)).save(argThat(
                x -> true ));
        Assertions.assertNotNull(output, "The object should have been created and returned");
        Assertions.assertEquals(0, output.getSubscribersCount(), "For a new community there should not be any subscribers");
        Assertions.assertEquals(0, output.getSubscribersCount(), "For a new community there should not be any subscribers");
        Assertions.assertNotNull(output.getRating(), "A rating object should have been created");
        Assertions.assertNull(output.getRating().getTeaching(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getContent(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getWorkload(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertEquals(communityDto.getModuleId(), output.getModuleId(), "The module ID should be set.");
        Assertions.assertEquals(communityDto.getName(), output.getName(), "The name should be set.");


    }

    @Test
    void getCommunities() {
    }

    @Test
    void getCommunity() {
    }

//    @Test
//    void getCommunityReviews() {
//    }

    @Test
    void getCommunityRatings() {
    }

    @Test
    void createCommunityRating() {
    }

    @Test
    void getCommunityRatingAverage() {
    }

    @Test
    void deleteCommunity() {
    }
}