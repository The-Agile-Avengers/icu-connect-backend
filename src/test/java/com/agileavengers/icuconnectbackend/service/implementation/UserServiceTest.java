package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.*;
import com.agileavengers.icuconnectbackend.model.*;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.StudyAreaRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
import com.agileavengers.icuconnectbackend.service.IUserService;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest()
class UserServiceTest {

    IUserService userService;

    @Mock
    CommunityRepository communityRepository;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    StudyAreaRepository studyAreaRepository;

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
        communityRepository = mock(CommunityRepository.class);
        userRepository = mock(UserRepository.class);
        ratingRepository = mock(RatingRepository.class);
        studyAreaRepository = mock(StudyAreaRepository.class);
        MappingService mappingService = new MappingService(userRepository, ratingRepository);
        CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);
        communityMapper.setMappingService(mappingService);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        StudyAreaMapper studyAreaMapper = Mappers.getMapper(StudyAreaMapper.class);
        userMapper.setMappingService(mappingService);
        RatingMapper ratingMapper = new RatingMapperImpl(userMapper);
        ratingMapper.setMappingService(mappingService);
        IStudyAreaService studyAreaService = new StudyAreaService(studyAreaMapper, studyAreaRepository);
        this.userService = new UserService(communityMapper, ratingMapper, userMapper, communityRepository, userRepository, ratingRepository, studyAreaService);
    }

    @Test
    void updateCommunityRelationRemove() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        HashSet<Community> subscription = new HashSet<>();
        subscription.add(community);
        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Set<CommunityDto> result = userService.updateCommunityRelation(user.getUsername(), community.getModuleId());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "The community should have been removed from the set");

    }

    @Test
    void updateCommunityRelationAdd() {

        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        HashSet<Community> subscription = new HashSet<>();
        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();

        setupSecurity(user);
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Set<CommunityDto> result = userService.updateCommunityRelation(user.getUsername(), community.getModuleId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size(), "The community shoul have been added to the set");

    }

    @Test
    void updateCommunityRelationError() {

        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.empty());

        User user = User.builder().username("Test1").password("anything").id(2L).build();

        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        try {
            userService.updateCommunityRelation(user.getUsername(), community.getModuleId());
            Assertions.fail("Community does not exist, should throw an error.");
        } catch (Exception ignored) {

        }

    }

    @Test
    void getJoinedCommunities() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        HashSet<Community> subscription = new HashSet<>();
        subscription.add(community);
        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();
        setupSecurity(user);

        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        Set<CommunityDto> result = userService.getJoinedCommunities(user.getUsername());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(subscription, user.getSubscriptionSet());


    }

    @Test
    void getCommunityRating() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        HashSet<Community> subscription = new HashSet<>();
        subscription.add(community);

        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();

        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        Rating rating = Rating.builder().community(community).creator(user).content(4.0).workload(1.0).teaching(3.0).text("Rating text").thumbsUp(Collections.<User>emptySet()).build();

        when(ratingRepository.findByCommunity_ModuleIdAndCreator_Id(community.getModuleId(), user.getId())).thenReturn(Optional.of(rating));

        RatingDto result = userService.getCommunityRating(user.getUsername(), community.getModuleId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(rating.getContent(), result.getContent(), "Value should not have changed");
        Assertions.assertEquals(rating.getTeaching(), result.getTeaching(), "Value should not have changed");
        Assertions.assertEquals(rating.getWorkload(), result.getWorkload(), "Value should not have changed");
        Assertions.assertEquals(0, result.getThumbsUp(), "Value should not have changed");
        Assertions.assertEquals(rating.getText(), result.getText(), "Value should not have changed");
        Assertions.assertEquals(rating.getCreation(), result.getCreation(), "Timestamp should be the same");
    }

    @Test
    void getCommunityRatingEmpty() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        HashSet<Community> subscription = new HashSet<>();
        subscription.add(community);

        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));


        when(ratingRepository.findByCommunity_ModuleIdAndCreator_Id(community.getModuleId(), user.getId())).thenReturn(Optional.empty());

        RatingDto result = userService.getCommunityRating(user.getUsername(), community.getModuleId());

        Assertions.assertNull(result);


    }

    @Test
    void getCommunityRatingError() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.empty());

        HashSet<Community> subscription = new HashSet<>();
        subscription.add(community);

        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(subscription).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        try {
            RatingDto result = userService.getCommunityRating(user.getUsername(), community.getModuleId());
            Assertions.fail("Community does not exist which should throw an error");
        } catch (Exception ignored) {

        }


    }

    @Test
    void getUser() {
        User user = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(null).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));

        UserDetailDto result = userService.getUser(user.getUsername());

        verify(userRepository, times(1)).findByUsername(argThat(
                x -> true ));

        Assertions.assertNotNull(result, "Returned user shoudld not be null");
        Assertions.assertEquals(user.getUsername(), result.getUsername(), "Should be username of user that is logged in.");
        Assertions.assertEquals(user.getEmail(), result.getEmail(), "Should be email of user that is logged in.");
        Assertions.assertEquals(user.getStudyArea(), result.getStudyArea(), "Should be study area of user that is logged in.");



    }

    @Test
    void updateUser() {
        User user = User.builder().username("Test1").email("test@uzh.ch").password("anything").id(2L).subscriptionSet(null).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        UserDetailDto details = UserDetailDto.builder().username("New Username").build();

        UserDetailDto result = userService.updateUser(user.getUsername(), details);

        // first time to get user, second time to verify user does not already exist
        verify(userRepository, times(2)).findByUsername(argThat(
                x -> true ));

        Assertions.assertNotNull(result, "Returned user shoudld not be null");
        Assertions.assertEquals(details.getUsername(), result.getUsername(), "Should be new username.");
        Assertions.assertEquals("test@uzh.ch", result.getEmail(), "Should still be old email.");
        Assertions.assertNull(result.getStudyArea(), "Should still be old study area.");
    }

    @Test
    void updateUser2() {
        User user = User.builder().username("Test1").email("test@uzh.ch").studyArea(StudyArea.builder().name("Old Study Area").build()).password("anything").id(2L).subscriptionSet(null).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(studyAreaRepository.save(Mockito.any(StudyArea.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        UserDetailDto details = UserDetailDto.builder().username("New Username").email("Anything@uzh.ch").studyArea(StudyArea.builder().name("Computer Science").build()).avatar("10").build();

        UserDetailDto result = userService.updateUser(user.getUsername(), details);

        // first time to get user, second time to verify user does not already exist
        verify(userRepository, times(2)).findByUsername(argThat(
                x -> true ));

        Assertions.assertNotNull(result, "Returned user should not be null");
        Assertions.assertEquals(details.getUsername(), result.getUsername(), "Should be new username.");
        Assertions.assertEquals(details.getEmail(), result.getEmail(), "Should be new email.");
        Assertions.assertEquals(details.getStudyArea(), result.getStudyArea(), "Should be new study area.");
        Assertions.assertEquals(details.getAvatar(), result.getAvatar(), "Should be new avatar.");
    }
}
