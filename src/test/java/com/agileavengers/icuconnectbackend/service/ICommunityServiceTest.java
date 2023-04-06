package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapperImpl;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.InstructorDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest()
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

    void setupSecurity(User user) {
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), new ArrayList<>()));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

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
        RatingMapper ratingMapper = new RatingMapperImpl(communityMapper);
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

        setupSecurity(User.builder().username("test1").password("pw").build());

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
        CommunityDto communityDto = CommunityDto.builder().name("Test Community").instructor(instructor).ects(6.0).moduleId("UZH1234").build();
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
        Assertions.assertEquals(communityDto.getEcts(), output.getEcts(), "ECTS should be set.");
        Assertions.assertFalse(output.getJoined(), "User should not have joined yet.");

    }

    @Test
    void createCommunityExistingInstructor() {

        setupSecurity(User.builder().username("test1").password("pw").build());

        when(communityRepository.save(Mockito.any(Community.class)))
                .thenAnswer(i -> {
                    Community argument = (Community) i.getArguments()[0];
                    argument.setId(1L);
                    return argument;
                });
        Instructor instructor = Instructor.builder().id(11L).name("Test Instructor").build();
        InstructorDto instructorDto = InstructorDto.builder().id(11L).name("Test Instructor").build();
        when(instructorRepository.findInstructorByName(instructor.getName()))
                .thenReturn(Optional.of(instructor));
        CommunityDto communityDto = CommunityDto.builder().name("Test Community").instructor(instructorDto).moduleId("UZH1234").build();
        CommunityDto output = communityService.createCommunity(communityDto);
        verify(communityRepository, times(1)).save(argThat(
                x -> true));
        verify(communityRepository, times(1)).save(argThat(
                x -> true));
        Assertions.assertNotNull(output, "The object should have been created and returned");
        Assertions.assertEquals(0, output.getSubscribersCount(), "For a new community there should not be any subscribers");
        Assertions.assertNotNull(output.getRating(), "A rating object should have been created");
        Assertions.assertNull(output.getRating().getTeaching(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getContent(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertNull(output.getRating().getWorkload(), "The ratings should be null, since no one has rated yet.");
        Assertions.assertEquals(communityDto.getModuleId(), output.getModuleId(), "The module ID should be set.");
        Assertions.assertEquals(communityDto.getName(), output.getName(), "The name should be set.");
        Assertions.assertFalse(output.getJoined(), "User should not have joined yet.");

    }

    @Test
    void createCommunityDuplicate() {

        Community community = Community.builder().moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        InstructorDto instructorDto = InstructorDto.builder().id(11L).name("Test Instructor").build();

        CommunityDto communityDto = CommunityDto.builder().name("Test Community").instructor(instructorDto).moduleId("UZH1234").build();
        try {
            CommunityDto output = communityService.createCommunity(communityDto);
            Assertions.fail("Module id is duplicated, should throw an error");
        } catch (Exception ignored) {

        }


    }

    @Test
    void getCommunitiesOnlyOne() {


        Instructor instructor = Instructor.builder().name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        User user = User.builder().username("test1").password("pw").subscriptionSet(Set.of(community)).build();
        setupSecurity(user);
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
        Assertions.assertNotNull(resultCommunity.getInstructor(), "Instructor should be set");
        Assertions.assertTrue(resultCommunity.getJoined(), "User should have joined the community");

    }

    @Test
    void getCommunitiesMultiple() {

        setupSecurity(User.builder().username("test1").password("pw").build());

        Instructor instructor = Instructor.builder().name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        List<Community> communityList = List.of(community, community, community);
        when(communityRepository.findAll(Mockito.any(Pageable.class)))
                .thenAnswer(i -> {
                    Pageable argument = (Pageable) i.getArguments()[0];
                    return new PageImpl<>(communityList.subList(argument.getPageNumber(), argument.getPageSize()), argument, communityList.size());
                });

        Page<CommunityDto> result = communityService.getCommunities(0, 2);

        Assertions.assertNotNull(result, "Page should not be null.");
        Assertions.assertEquals(3L, result.getTotalElements(), "Result should contain three elements.");
        Assertions.assertEquals(2, result.getTotalPages(), "Result should contain two pages.");
        Assertions.assertEquals(2, result.getContent().size(), "Result should contain two elements.");

    }

    @Test
    void getCommunity() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        setupSecurity(user1);
        User user2 = User.builder().username("Test2").password("anything").id(3L).build();

        when(userRepository.countAllBysubscriptionSetContaining(community))
                .thenAnswer(i -> 1);

        Rating rating = Rating.builder().creator(user1).community(community).content(4.0).workload(3.0).teaching(1.0).build();
        Rating rating2 = Rating.builder().creator(user2).community(community).content(5.0).workload(2.0).teaching(4.0).build();
        when(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId()))
                .thenAnswer(i -> List.of(rating, rating2));

        CommunityDto result = communityService.getCommunity(community.getModuleId());

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
        when(communityRepository.findCommunityByModuleId("noExist"))
                .thenAnswer(i -> Optional.empty());

        try {
            communityService.getCommunity("noExist");
            Assertions.fail("Community does not exist. Should throw an error");
        } catch (Exception ignore) {}
    }

    @Test
    void getCommunityRatings() {
        Instructor instructor = Instructor.builder().id(2L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        setupSecurity(user1);

        User user2 = User.builder().username("Test2").password("anything").id(3L).build();

        Rating rating = Rating.builder().creator(user1).community(community).content(4.0).workload(3.0).teaching(1.0).build();
        Rating rating2 = Rating.builder().creator(user2).community(community).content(5.0).workload(2.0).teaching(4.0).build();
        List<Rating> ratingList = List.of(rating, rating2);

        Pageable pageable = PageRequest.of(0, 1);

        when(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId(), pageable))
                .thenAnswer(i -> {
                    Pageable argument = (Pageable) i.getArguments()[1];
                    return new PageImpl<>(ratingList.subList(argument.getPageNumber(), argument.getPageSize()), argument, ratingList.size());
                });

        Page<RatingDto> result = communityService.getCommunityRatings(community.getModuleId(), pageable.getPageNumber(), pageable.getPageSize());

        Assertions.assertNotNull(result, "Page should not be null");
        Assertions.assertEquals(2L, result.getTotalElements(), "Result should contain two elements.");
        Assertions.assertEquals(2, result.getTotalPages(), "Result should contain two pages.");
        Assertions.assertEquals(1, result.getContent().size(), "Result page should contain one element.");

    }

    @Test
    void getCommunityRatingsException() {
        Instructor instructor = Instructor.builder().id(2L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.empty());

        Pageable pageable = PageRequest.of(0, 1);

        try {
            communityService.getCommunityRatings(community.getModuleId(), pageable.getPageNumber(), pageable.getPageSize());
            Assertions.fail("Community does not exist and an error should be thrown.");
        } catch (Exception ignored) {

        }

    }

    @Test
    void createCommunityRating() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));


        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        setupSecurity(user1);
        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        when(ratingRepository.save(Mockito.any(Rating.class)))
            .thenAnswer(i -> {
                Rating argument = (Rating) i.getArguments()[0];
                argument.setId(11L);
                return argument;
            });

        RatingDto rating = RatingDto.builder().workload(3.0).content(1.0).teaching(5.0).text("Average course").build();

        RatingDto result = communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());

        Assertions.assertNotNull(result, "Returned object should not be null");
        Assertions.assertEquals(rating.getContent(), result.getContent(), "Fields should not have changed");
        Assertions.assertEquals(rating.getTeaching(), result.getTeaching(), "Fields should not have changed");
        Assertions.assertEquals(rating.getWorkload(), result.getWorkload(), "Fields should not have changed");
        Assertions.assertEquals(0, result.getThumbsUp(), "Thumbs up should be 0");
        Assertions.assertNotNull(result.getId(), "Id should nto be null");


    }

    @Test
    void createCommunityRatingDouble() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        Rating rating = Rating.builder().creator(user1).workload(3.0).content(1.0).teaching(5.0).text("Average course").community(community).build();


        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        when(ratingRepository.findByCommunity_ModuleIdAndCreator_Id(community.getModuleId(), user1.getId())).thenReturn(Optional.of(rating));

        RatingDto ratingDto = RatingDto.builder().workload(3.0).content(1.0).teaching(5.0).text("Average course").build();

        try {
            communityService.createCommunityRating(community.getModuleId(), ratingDto, user1.getUsername());
            Assertions.fail("Double rating creatio should not be possible.");
        } catch (Exception ignored) {
        }


    }

    @Test
    void createCommunityRatingNoUser() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();


        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.empty());

        RatingDto rating = RatingDto.builder().workload(3.0).content(1.0).teaching(5.0).text("Average course").build();

        try {
            communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());
            Assertions.fail("User does not exist and error should be thrown");
        } catch (Exception ignore) {}
    }

    @Test
    void createCommunityRatingNoCommunity() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
            .thenAnswer(i -> Optional.empty());

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        RatingDto rating = RatingDto.builder().workload(3.0).content(1.0).teaching(5.0).text("Average course").build();

        try {
            communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());
            Assertions.fail("Community does not exist and error should be thrown");
        } catch (Exception ignore) {}
    }

    @Test
    void createCommunityRatingWrongRange() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
            .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        RatingDto rating = RatingDto.builder().workload(-3.0).content(1.0).teaching(5.0).text("Average course").build();

        try {
            communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());
            Assertions.fail("Rating workload is out of range and error should be thrown");
        } catch (Exception ignore) {
        }
    }

    @Test
    void createCommunityRatingIncomplete() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        RatingDto rating = RatingDto.builder().workload(3.0).teaching(5.0).text("Average course").build();

        try {
            communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());
            Assertions.fail("Not all categories were rated, should throw an error.");
        } catch (Exception ignore) {
        }
    }

    @Test
    void getCommunityRatingAverage() {

        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        User user2 = User.builder().username("Test2").password("anything").id(3L).build();
        Rating rating = Rating.builder().creator(user1).community(community).content(5.0).workload(3.0).teaching(2.0).build();
        Rating rating2 = Rating.builder().creator(user2).community(community).content(5.0).workload(2.0).teaching(4.0).build();
        when(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId()))
                .thenAnswer(i -> List.of(rating, rating2));

        RatingAverage result = communityService.getCommunityRatingAverage(community.getModuleId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5.0, result.getContent(), "Content rating should be equal to average.");
        Assertions.assertEquals(2.5, result.getWorkload(), "Workload rating should be equal to average.");
        Assertions.assertEquals(3.0, result.getTeaching(), "Teaching rating should be equal to average.");


    }

}
