package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.config.JwtTokenUtil;
import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapperImpl;
import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.IUserService;
import com.agileavengers.icuconnectbackend.service.JwtUserDetailsService;
import org.apache.coyote.Response;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest()
class UserServiceTest {

    IUserService userService;

    JwtTokenUtil jwtTokenUtil;

    JwtUserDetailsService userDetailsService;

    @Mock
    CommunityRepository communityRepository;
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
        communityRepository = mock(CommunityRepository.class);
        userRepository = mock(UserRepository.class);
        ratingRepository = mock(RatingRepository.class);
        MappingService mappingService = new MappingService(userRepository, ratingRepository);
        CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);
        communityMapper.setMappingService(mappingService);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        userMapper.setMappingService(mappingService);
        RatingMapper ratingMapper = new RatingMapperImpl(userMapper);
        ratingMapper.setMappingService(mappingService);
        jwtTokenUtil = new JwtTokenUtil();
        userDetailsService = new JwtUserDetailsService(userRepository, userMapper);
        this.userService = new UserService(communityMapper, ratingMapper, userMapper, communityRepository, userRepository, ratingRepository, userDetailsService, jwtTokenUtil);
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

        Rating rating = Rating.builder().community(community).creator(user).content(4.0).workload(1.0).teaching(3.0).text("Rating text").build();

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

        ResponseEntity<UserDetailDto> result = userService.updateUser(user.getUsername(), details);

        verify(userRepository, times(1)).findByUsername(argThat(
                x -> true ));



        Assertions.assertNotNull(result, "Returned user shoudld not be null");
        Assertions.assertTrue(result.hasBody(), "Body should not be null");
        UserDetailDto body = result.getBody();
        Assertions.assertEquals(details.getUsername(), body.getUsername(), "Should be new username.");
        Assertions.assertEquals("test@uzh.ch", body.getEmail(), "Should still be old email.");
        Assertions.assertNull(body.getStudyArea(), "Should still be old study area.");
        Assertions.assertTrue(result.getHeaders().containsKey("token"), "Response should contain header with token");
    }

    @Test
    void updateUser2() {
        User user = User.builder().username("Test1").email("test@uzh.ch").studyArea("Old Study Area").password("anything").id(2L).subscriptionSet(null).build();
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i -> Optional.of(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        UserDetailDto details = UserDetailDto.builder().username("New Username").email("Anything@uzh.ch").studyArea("Computer Science").avatar("10").build();

        User expectedUser = User.builder().username(details.getUsername()).email(details.getEmail()).studyArea(details.getStudyArea()).avatar(details.getAvatar()).password("anything").id(2L).subscriptionSet(null).build();
        // first call return empty, then return new object
        when(userRepository.findByUsername(expectedUser.getUsername())).thenReturn(Optional.empty(), Optional.of(expectedUser));
        ResponseEntity<UserDetailDto> result = userService.updateUser(user.getUsername(), details);

        verify(userRepository, times(2)).findByUsername(argThat(
                x -> true ));

        Assertions.assertNotNull(result, "Returned user should not be null");
        Assertions.assertTrue(result.hasBody(), "Body should not be null");
        UserDetailDto body = result.getBody();
        Assertions.assertEquals(details.getUsername(), body.getUsername(), "Should be new username.");
        Assertions.assertEquals(details.getEmail(), body.getEmail(), "Should be new email.");
        Assertions.assertEquals(details.getStudyArea(), body.getStudyArea(), "Should be new study area.");
        Assertions.assertEquals(details.getAvatar(), body.getAvatar(), "Should be new avatar.");
    }
}
