package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.IcuConnectBackendApplication;
import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.implementation.CommunityService;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
//@SpringBootTest(classes = {CommunityMapperImpl.class, RatingMapperImpl.class, InstructorMapperImpl.class})
//@ContextConfiguration(classes = { CommunityMapperImpl.class, RatingMapperImpl.class, InstructorMapperImpl.class,
//        UserRepository.class, CommunityRepository.class, InstructorRepository.class, RatingRepository.class
//
//})
@SpringBootTest()
@ComponentScan(basePackageClasses = IcuConnectBackendApplication.class)
class ICommunityServiceTest {

    ICommunityService communityService;

    @Mock
    CommunityRepository communityRepository;
    @Mock
    InstructorRepository instructorRepository;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    UserRepository userRepository;

//    private CommunityMapper communityMapper
//            = Mappers.getMapper(CommunityMapper.class);
//    private ReviewMapper reviewMapper
//            = Mappers.getMapper(ReviewMapper.class);
//    private RatingMapper ratingMapper
//            = Mappers.getMapper(RatingMapper.class);
//    private InstructorMapper instructorMapper
//            = Mappers.getMapper(InstructorMapper.class);

//    private MappingService mappingService;
//
//    private CommunityMapper communityMapper;
//
//    private RatingMapper ratingMapper;
//
//    private InstructorMapper instructorMapper;


    @BeforeEach
    void setup() {
        communityRepository = mock(CommunityRepository.class);
        instructorRepository = mock(InstructorRepository.class);
        communityRepository = mock(CommunityRepository.class);
        userRepository = mock(UserRepository.class);
        ratingRepository = mock(RatingRepository.class);
        MappingService mappingService = new MappingService(userRepository, ratingRepository);
        CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);
        communityMapper.setMappingService(mappingService);
        RatingMapper ratingMapper = Mappers.getMapper(RatingMapper.class);
        ratingMapper.setMappingService(mappingService);
        InstructorMapper instructorMapper = Mappers.getMapper(InstructorMapper.class);
        this.communityService = new CommunityService(communityRepository, instructorRepository, ratingRepository, userRepository,
                communityMapper, ratingMapper, instructorMapper
        );
    }

    @Test
    void setupExampleCommunity() {
    }

    @Test
    void createCommunityNewInstructor() {

        when(instructorRepository.save(Mockito.any(Instructor.class)))
                .thenAnswer(i -> {
                    Instructor argument = (Instructor) i.getArguments()[0];
                    argument.setId(2L);
                    return argument;
                });
        when(communityRepository.save(Mockito.any(Community.class)))
                .thenAnswer(i -> {
                    Community argument = (Community) i.getArguments()[0];
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
        Assertions.assertNotNull(output.getRating(), "A rating object should have been created");
        Assertions.assertNull(output.getRating().getTeaching(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getContent(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getWorkload(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertEquals(communityDto.getModuleId(), output.getModuleId(), "The module ID should be set.");
        Assertions.assertEquals(communityDto.getName(), output.getName(), "The name should be set.");

    }

    @Test
    void getCommunitiesOnlyOne() {


        Instructor instructor = Instructor.builder().name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        List<Community> communityList = List.of(community);
        when(communityRepository.findAll(Mockito.any(Pageable.class)))
                .thenAnswer(i -> {
                    Pageable argument = (Pageable) i.getArguments()[0];
                    return new PageImpl<>(communityList, argument, communityList.size());
                });

        Page<CommunityDto> result = communityService.getCommunities(0, 5);

        Assertions.assertNotNull(result, "Page should not be null.");
        Assertions.assertEquals(1L, result.getTotalElements(), "Result should only contain one element.");
        Assertions.assertEquals(1, result.getTotalPages(), "Result should only contain one page.");
        Assertions.assertEquals(1, result.getContent().size(), "Result should contain one element.");
        CommunityDto resultCommunity = result.getContent().get(0);
        Assertions.assertEquals(community.getName(), resultCommunity.getName(), "Name should be equal to prepared community");
        Assertions.assertEquals(community.getModuleId(), resultCommunity.getModuleId(), "ModuleId should be equal to prepared community");
        Assertions.assertEquals(community.getId(), resultCommunity.getId(), "Id should be equal to prepared community");
        Assertions.assertNotNull(resultCommunity.getInstructor(), "Instructor should be set");

    }

    @Test
    void getCommunitiesMultiple() {


        Instructor instructor = Instructor.builder().name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        List<Community> communityList = List.of(community, community, community);
        when(communityRepository.findAll(Mockito.any(Pageable.class)))
                .thenAnswer(i -> {
                    Pageable argument = (Pageable) i.getArguments()[0];
                    return new PageImpl<>(communityList.subList(0, 2), argument, communityList.size());
                });

        Page<CommunityDto> result = communityService.getCommunities(0, 2);

        Assertions.assertNotNull(result, "Page should not be null.");
        Assertions.assertEquals(3L, result.getTotalElements(), "Result should contain three elements.");
        Assertions.assertEquals(2, result.getTotalPages(), "Result should contain two pages.");
        Assertions.assertEquals(2, result.getContent().size(), "Result should contain two elements.");

    }

    @Test
    void getCommunity() {
        Instructor instructor = Instructor.builder().name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findById(1L))
                .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionList(List.of(community)).build();

        User user2 = User.builder().username("Test2").password("anything").id(3L).build();

        when(userRepository.countAllBySubscriptionListContaining(community))
                .thenAnswer(i -> 1);

        Rating rating = Rating.builder().creator(user1).community(community).content(4).workload(3).teaching(1).build();
        Rating rating2 = Rating.builder().creator(user2).community(community).content(5).workload(2).teaching(4).build();
        when(ratingRepository.findAllByCommunity_Id(1L))
                .thenAnswer(i -> List.of(rating, rating2));

        CommunityDto result = communityService.getCommunity(1L);

        Assertions.assertNotNull(result, "Result should not be null.");
        Assertions.assertEquals(community.getModuleId(), result.getModuleId(), "Result should have same module id.");

        Assertions.assertNotNull(result.getRating(), "Rating should not be null.");
        RatingAverage avg = result.getRating();
        Assertions.assertEquals(4.5, avg.getContent(), "Content rating should be equal to average.");
        Assertions.assertEquals(2.5, avg.getWorkload(), "Workload rating should be equal to average.");
        Assertions.assertEquals(2.5, avg.getTeaching(), "Teaching rating should be equal to average.");

        Assertions.assertEquals(1, result.getSubscribersCount(), "Only one subscriber exists");

    }

    @Test
    void getCommunityEmpty() {
//        Instructor instructor = Instructor.builder().name("Test Instructor").build();
//        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findById(2L))
                .thenAnswer(i -> Optional.empty());

        try {
            communityService.getCommunity(2L);
            Assertions.fail("Community does not exist. Should throw an error");
        } catch (Exception ignore) {}
    }

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