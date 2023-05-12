package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.mapper.*;
import com.agileavengers.icuconnectbackend.model.*;
import com.agileavengers.icuconnectbackend.model.dto.*;
import com.agileavengers.icuconnectbackend.repository.*;
import com.agileavengers.icuconnectbackend.service.implementation.CommunityService;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.junit.gen5.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest()
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;

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
        commentRepository = mock(CommentRepository.class);
        MappingService mappingService = new MappingService(userRepository, ratingRepository);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        userMapper.setMappingService(mappingService);
        RatingMapper ratingMapper = new RatingMapperImpl(userMapper);
        ratingMapper.setMappingService(mappingService);
        InstructorMapper instructorMapper = Mappers.getMapper(InstructorMapper.class);
        postRepository = mock(PostRepository.class);
        CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);
        communityMapper.setMappingService(mappingService);
        CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
        commentMapper.setMappingService(mappingService);
        PostMapper postMapper = Mappers.getMapper(PostMapper.class);
        postMapper.setMappingService(mappingService, commentRepository, commentMapper);

        this.communityService = new CommunityService(communityRepository, instructorRepository, ratingRepository, userRepository,
                communityMapper, ratingMapper, instructorMapper, postRepository, postMapper, commentMapper, commentRepository);
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

        Page<CommunityDto> result = communityService.getCommunities(0, 5, Optional.empty());

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

        Page<CommunityDto> result = communityService.getCommunities(0, 2, Optional.empty());

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

        User user2 = User.builder().username("Test2").password("anything").id(3L).build();
        
        setupSecurity(user1);
        setupSecurity(user2);
        
        Rating rating = Rating.builder().creator(user1).community(community).content(4.0).workload(3.0).teaching(1.0).thumbsUp(Collections.<User>emptySet()).build();
        Rating rating2 = Rating.builder().creator(user2).community(community).content(5.0).workload(2.0).teaching(4.0).thumbsUp(Collections.<User>emptySet()).build();
        List<Rating> ratingList = List.of(rating, rating2);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("thumbsUp").descending());
        
        when(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId(), pageable))
                .thenAnswer(i -> {
                    Pageable argument = (Pageable) i.getArguments()[1];
                    return new PageImpl<>(ratingList.subList(argument.getPageNumber(), argument.getPageSize()), argument, ratingList.size());
                });

   
        Page<RatingDto> result = communityService.getCommunityRatings(community.getModuleId(), pageable.getPageNumber(), pageable.getPageSize(), Optional.of(Boolean.TRUE));

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
            communityService.getCommunityRatings(community.getModuleId(), pageable.getPageNumber(), pageable.getPageSize(), Optional.empty());
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

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        when(ratingRepository.save(Mockito.any(Rating.class)))
            .thenAnswer(i -> {
                Rating argument = (Rating) i.getArguments()[0];
                argument.setId(11L);
                return argument;
            });
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        setupSecurity(user1);
        RatingDto rating = RatingDto.builder().workload(3.0).content(1.0).teaching(5.0).text("Average course").hasLiked(false).build();
        
        RatingDto result = communityService.createCommunityRating(community.getModuleId(), rating, user1.getUsername());

        Assertions.assertNotNull(result, "Returned object should not be null");
        Assertions.assertEquals(rating.getContent(), result.getContent(), "Fields should not have changed");
        Assertions.assertEquals(rating.getTeaching(), result.getTeaching(), "Fields should not have changed");
        Assertions.assertEquals(rating.getWorkload(), result.getWorkload(), "Fields should not have changed");
        Assertions.assertEquals(0, result.getThumbsUp(), "Thumbs up should be 0");
        Assertions.assertNotNull(result.getId(), "Id should not be null");
        Assertions.assertNotNull(result.getCreation(), "Creation should not be null");

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

    @Test
    void createCommunityPost() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));


        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        Post post = Post.builder().id(2L).title("Question 1 Title").text("Question 1 Text").community(community).creation(new Timestamp(System.currentTimeMillis())).creator(user1).build();

        PostDto postDto = PostDto.builder().id(2L).title("Question 1 Title").text("Question 1 Text")
        .build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        when(postRepository.save(Mockito.any(Post.class)))
            .thenAnswer(i -> {
                Post argument = (Post) i.getArguments()[0];
                argument.setId(1L);
                return argument;
            });

        PostDto result = communityService.createPost(community.getModuleId(), postDto, user1.getUsername());

        UserDto userDto = UserDto.builder().id(user1.getId()).username(user1.getUsername()).build();


        Assertions.assertNotNull(result, "Returned object should not be null");
        Assertions.assertEquals(userDto, result.getUser(), "Fields should not have changed");
        Assertions.assertEquals(post.getText(), result.getText(), "Fields should not have changed");
        Assertions.assertEquals(post.getTitle(), result.getTitle(), "Fields should not have changed");
        Assertions.assertNotNull(result.getId(), "Id should not be null");

    }

    @Test
    void getCommunityPosts() {

        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        User user2 = User.builder().username("Test2").password("anything").id(3L).build();
        Post post = Post.builder().creator(user1).community(community).title("Test Title 1").text("Test text 1").creation(new Timestamp(System.currentTimeMillis())).build();
        Post post2 = Post.builder().creator(user2).community(community).title("Test Title 2").text("Test text 2").creation(new Timestamp(System.currentTimeMillis())).build();

        List<Post> postList = List.of(post,post2);

        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        when(postRepository.findAllByCommunity_ModuleId(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenAnswer(i -> {
                        Pageable argument = (Pageable) i.getArguments()[1];
                        return new PageImpl<>(postList.subList(argument.getPageNumber(), argument.getPageSize()), argument, postList.size());
                });

        Page<PostDto> result = communityService.getCommunityPosts(community.getModuleId(), 0,2, Optional.empty());

        Assertions.assertNotNull(result, "Page should not be null.");
        Assertions.assertEquals(2L, result.getTotalElements(), "Result should contain two elements.");
        Assertions.assertEquals(1, result.getTotalPages(), "Result should contain one page.");
        Assertions.assertEquals(2, result.getContent().size(), "Result should contain two elements.");
    }

    @Test
    void getCommunityPostsEmpty() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            communityService.getCommunityPosts("noExist", 0, 2, Optional.empty());
        }, "ResponeStatusException");

        Assertions.assertEquals("community does not exist", exception.getReason());
    }

    @Test
    void createCommentExistingPost() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));


        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        UserDto userDto = UserDto.builder().id(user1.getId()).username(user1.getUsername()).build();

        Post post = Post.builder().id(2L).title("Question 1 Title").text("Question 1 Text").community(community).creation(new Timestamp(System.currentTimeMillis())).creator(user1).build();

        PostDto postDto = PostDto.builder().id(2L).title("Question 1 Title").text("Question 1 Text")
        .build();

        CommentDto commentDto = CommentDto.builder().id(1L).creation(new Timestamp(System.currentTimeMillis())).user(userDto).text("Test comment text").build();


        when(postRepository.findByIdAndCommunity_ModuleId(postDto.getId(), community.getModuleId()))
            .thenAnswer(i -> Optional.of(post));

        when(commentRepository.save(Mockito.any(Comment.class)))
            .thenAnswer(i -> {
                Comment argument = (Comment) i.getArguments()[0];
                argument.setId(1L);
                return argument;
            });

        CommentDto result = communityService.createComment(community.getModuleId(), post.getId(), commentDto, user1.getUsername());

        Assertions.assertNotNull(result, "Returned object should not be null");
        Assertions.assertEquals(userDto, result.getUser(), "Fields should not have changed");
        Assertions.assertEquals(commentDto.getText(), result.getText(), "Fields should not have changed");
        Assertions.assertNotNull(result.getId(), "Id should not be null");

    }

    @Test
    void getCommunityPostsWithComments() {

        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();
        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();
        Post post = Post.builder().id(1L).creator(user1).community(community).title("Test Title 1").text("Test text 1").creation(new Timestamp(System.currentTimeMillis())).build();
        Post post2 = Post.builder().id(2L).creator(user1).community(community).title("Test Title 2").text("Test text 2").creation(new Timestamp(System.currentTimeMillis())).build();
        
        UserDto userDto = UserDto.builder().id(user1.getId()).username(user1.getUsername()).build();

        CommentDto commentDto = CommentDto.builder().id(1L).creation(new Timestamp(System.currentTimeMillis())).user(userDto).text("Test comment text").build();

        Comment comment = Comment.builder().id(1L).creation(new Timestamp(System.currentTimeMillis())).creator(user1).text("Test comment text").post(post).build();

        List<Post> postList = List.of(post,post2);
       
        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));

        when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                .thenAnswer(i -> Optional.of(community));

        when(postRepository.findAllByCommunity_ModuleId(Mockito.anyString(), Mockito.any(Pageable.class)))
            .thenAnswer(i -> {
                Pageable argument = (Pageable) i.getArguments()[1];
                return new PageImpl<>(postList.subList(argument.getPageNumber(), argument.getPageSize()), argument, postList.size());
        });

        when(postRepository.findByIdAndCommunity_ModuleId(post.getId(), community.getModuleId())).thenAnswer(i -> Optional.of(post));

        when(commentRepository.save(Mockito.any(Comment.class)))
            .thenAnswer(i -> comment);

        when(commentRepository.findAllByPost_Id(post.getId()))
            .thenAnswer(i -> List.of(comment));
        
            

        communityService.createComment(community.getModuleId(), post.getId(), commentDto, user1.getUsername());

        Page<PostDto> result = communityService.getCommunityPosts(community.getModuleId(), 0,2, Optional.empty());


        Assertions.assertNotNull(result, "Page should not be null.");
        Assertions.assertEquals(2L, result.getTotalElements(), "Result should contain two elements.");
        Assertions.assertEquals(1, result.getTotalPages(), "Result should contain one page.");
        Assertions.assertEquals(2, result.getContent().size(), "Result should contain two elements.");
        assertThat(commentDto).usingRecursiveComparison().ignoringFields("creation").isEqualTo(result.getContent().get(0).getCommentList().get(0));
    }

    @Test
    public void deletePostNoComments() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        Post post = Post.builder().id(1L).creator(user1).community(community).title("Test Title 1").text("Test text 1").creation(new Timestamp(System.currentTimeMillis())).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));
        when(postRepository.findByIdAndCommunity_ModuleId(post.getId(), community.getModuleId())).thenAnswer(i -> Optional.of(post));
        when(commentRepository.findAllByPost_Id(post.getId())).thenAnswer(i -> List.of());

        communityService.deletePost(community.getModuleId(), post.getId(), user1.getUsername());

        verify(commentRepository, times(0)).delete(Mockito.any(Comment.class));
        verify(postRepository, times(1)).delete(post);


    }

    @Test
    public void deletePostWithComments() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        Post post = Post.builder().id(1L).creator(user1).community(community).title("Test Title 1").text("Test text 1").creation(new Timestamp(System.currentTimeMillis())).build();

        Comment comment = Comment.builder().id(1L).creation(new Timestamp(System.currentTimeMillis())).creator(user1).text("Test comment text").post(post).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));
        when(postRepository.findByIdAndCommunity_ModuleId(post.getId(), community.getModuleId())).thenAnswer(i -> Optional.of(post));
        when(commentRepository.findAllByPost_Id(post.getId())).thenAnswer(i -> List.of(comment));

        InOrder inOrder = Mockito.inOrder(commentRepository, postRepository);

        communityService.deletePost(community.getModuleId(), post.getId(), user1.getUsername());

        inOrder.verify(commentRepository, times(1)).delete(comment);
        inOrder.verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void thumbsUpCounterUpdate() {
        Instructor instructor = Instructor.builder().id(10L).name("Test Instructor").build();
        Community community = Community.builder().id(1L).name("Test Community").instructor(instructor).moduleId("UZH1234").build();

        User user1 = User.builder().username("Test1").password("anything").id(2L).subscriptionSet(Set.of(community)).build();

        setupSecurity(user1);
        Rating rating = Rating.builder().creator(user1).community(community).content(5.0).workload(3.0).teaching(2.0).thumbsUp(new HashSet<User>()).build();

        when(userRepository.findByUsername(user1.getUsername())).thenAnswer(i -> Optional.of(user1));
        
        when(ratingRepository.findByIdAndCommunity_ModuleId(rating.getId(), community.getModuleId())).thenAnswer(i -> Optional.of(rating));
        
        when(ratingRepository.save(Mockito.any(Rating.class)))
        .thenAnswer(i -> rating);
        
        
        RatingDto result = communityService.thumbsUp(community.getModuleId(), rating.getId(), user1.getUsername());

        Assertions.assertSame(1, result.getThumbsUp(), "Result should have one thumbs up");
        Assertions.assertTrue(result.getHasLiked());


        result = communityService.thumbsUp(community.getModuleId(), rating.getId(), user1.getUsername());

        Assertions.assertSame(0, result.getThumbsUp(), "Result should have zero thumbs up");
        Assertions.assertFalse(result.getHasLiked());

    }
}
