package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.PostMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.model.*;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.repository.*;
import com.agileavengers.icuconnectbackend.service.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityService implements ICommunityService {

    private final CommunityMapper communityMapper;
    private final RatingMapper ratingMapper;
    private final InstructorMapper instructorMapper;
    private final PostMapper postMapper;
    PostRepository postRepository;
    CommunityRepository communityRepository;
    InstructorRepository instructorRepository;
    RatingRepository ratingRepository;
    UserRepository userRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository,
            InstructorRepository instructorRepository, RatingRepository ratingRepository,
            UserRepository userRepository, CommunityMapper communityMapper, RatingMapper ratingMapper,
            InstructorMapper instructorMapper, PostRepository postRepository, PostMapper postMapper) {
        this.communityRepository = communityRepository;
        this.instructorRepository = instructorRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.instructorMapper = instructorMapper;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public CommunityDto setupExampleCommunity() {
        Instructor instructor = Instructor.builder().name("Anna King").build();
        instructor = instructorRepository.save(instructor);

        Community community = Community.builder().name("Lecture 1").moduleId("UZH123").instructor(instructor).build();
        community = communityRepository.save(community);

        User user = User.builder().username("Test").password("password").build();
        userRepository.save(user);

        createCommunityRating(community.getModuleId(),
                RatingDto.builder().teaching(3.0).content(2.5).workload(5.0).build(),
                user.getUsername());

        return communityMapper.toDto(community);
    }

    @Override
    public CommunityDto createCommunity(CommunityDto communityDto) {
        Community community = new Community();
        if (communityDto.getModuleId() != null) {
            if (communityRepository.findCommunityByModuleId(communityDto.getModuleId())
                    .isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module ID already exists");
            }
            community.setModuleId(communityDto.getModuleId());
        }
        if (communityDto.getInstructor() != null) {
            Optional<Instructor> instructor = instructorRepository
                    .findInstructorByName(communityDto.getInstructor().getName());
            if (instructor.isPresent()) {
                community.setInstructor(instructor.get());
            } else {
                community.setInstructor(instructorRepository.save(
                        instructorMapper.fromDto(communityDto.getInstructor())));
            }
        }
        community.setName(communityDto.getName());
        community.setEcts(communityDto.getEcts());
        return communityMapper.toDto(communityRepository.save(community));
    }

    @Override
    public Page<CommunityDto> getCommunities(int page, int size, Optional<String> search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Community> communityPage;
        if (search.isPresent()) {
            String query = search.get();
            communityPage = communityRepository
                    .findAllByNameContainingOrModuleIdContainingOrInstructor_NameContaining(pageable, query, query, query);
        } else {
            communityPage = communityRepository.findAll(pageable);
        }
        return communityPage.map(communityMapper::toDto);
    }

    @Override
    public CommunityDto getCommunity(String moduleId) {
        Optional<Community> communityOptional = this.communityRepository.findCommunityByModuleId(moduleId);
        if (communityOptional.isPresent()) {
            return communityMapper.toDto(communityOptional.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }

    @Override
    public Page<RatingDto> getCommunityRatings(String moduleId, int page, int size, Optional<Boolean> sortByMostLiked) {
        Sort sort = Sort.by("thumbsUp").descending();
        if (sortByMostLiked.isPresent() && !sortByMostLiked.get()) {
            sort = Sort.by("creation").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        Page<Rating> ratingPage = ratingRepository.findAllByCommunity_ModuleId(moduleId, pageable);
        return ratingPage.map(ratingMapper::toDto);
    }

    @Override
    public RatingDto createCommunityRating(String moduleId, RatingDto ratingDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        if (ratingRepository.findByCommunity_ModuleIdAndCreator_Id(community.get().getModuleId(), user.get().getId())
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already rated this community");
        }
        if (ratingDto.getTeaching() == null || ratingDto.getContent() == null
                || ratingDto.getWorkload() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "not all categories were rated");
        }
        if (ratingDto.getTeaching() < 0.0 || ratingDto.getTeaching() > 5.0
                || ratingDto.getWorkload() < 0.0 || ratingDto.getWorkload() > 5.0
                || ratingDto.getContent() < 0.0 || ratingDto.getContent() > 5.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ratings are not within range");
        }

        Rating rating = ratingMapper.fromDto(ratingDto);
        rating.setCommunity(community.get());
        rating.setCreator(user.get());
        rating.setCreation(new Timestamp(System.currentTimeMillis()));

        return ratingMapper.toDto(ratingRepository.save(rating));
    }

    @Override
    public RatingAverage getCommunityRatingAverage(String moduleId) {
        List<Rating> ratingList = ratingRepository.findAllByCommunity_ModuleId(moduleId);
        return new RatingAverage(ratingList);
    }

    @Override
    public void deleteCommunity(String moduleId) {
        // TODO: make sure requesting user has rights to do so

        if (communityRepository.existsByModuleId(moduleId)) {
            communityRepository.deleteCommunityByModuleId(moduleId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @Override
    public PostDto createPost(String moduleId, PostDto postDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }

        Post post = postMapper.fromDto(postDto);
        post.setCreator(user.get());

        post.setCreation(new Timestamp(System.currentTimeMillis()));

        post.setCommunity(community.get());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public Page<PostDto> getCommunityPosts(String moduleId, int pageNumber, int size) {
        Pageable page = PageRequest.of(pageNumber, size);
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }
        Page<Post> postPage = postRepository.findAllByCommunity_ModuleId(moduleId, page);
        return postPage.map(postMapper::toDto);
    }
}
