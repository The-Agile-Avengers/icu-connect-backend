package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityService implements ICommunityService {

    private final CommunityMapper communityMapper;
    private final RatingMapper ratingMapper;
    private final InstructorMapper instructorMapper;
    CommunityRepository communityRepository;
    InstructorRepository instructorRepository;
    RatingRepository ratingRepository;
    UserRepository userRepository;


    @Autowired
    public CommunityService(CommunityRepository communityRepository,
        InstructorRepository instructorRepository, RatingRepository ratingRepository,
        UserRepository userRepository, CommunityMapper communityMapper, RatingMapper ratingMapper,
        InstructorMapper instructorMapper) {
        this.communityRepository = communityRepository;
        this.instructorRepository = instructorRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public CommunityDto setupExampleCommunity() {
        Instructor instructor = Instructor.builder().name("Anna King").build();
        instructor = instructorRepository.save(instructor);

        Community community =
            Community.builder().name("Lecture 1").moduleId("UZH123").instructor(instructor).build();
        community = communityRepository.save(community);

        User user = User.builder().username("Test").password("password").build();
        userRepository.save(user);

        createCommunityRating(community.getId(),
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
            Optional<Instructor> instructor =
                instructorRepository.findInstructorByName(communityDto.getInstructor().getName());
            if (instructor.isPresent()) {
                community.setInstructor(instructor.get());
            } else {
                community.setInstructor(instructorRepository.save(
                    instructorMapper.fromDto(communityDto.getInstructor())));
            }
        }
        community.setName(communityDto.getName());
        return communityMapper.toDto(communityRepository.save(community));
    }

    @Override
    public Page<CommunityDto> getCommunities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Community> communityPage = communityRepository.findAll(pageable);
        return communityPage.map(communityMapper::toDto);
    }

    @Override
    public CommunityDto getCommunity(Long id) {
        Optional<Community> communityOptional = this.communityRepository.findById(id);
        if (communityOptional.isPresent()) {
            return communityMapper.toDto(communityOptional.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }

    //    @Override
    //    public Page<ReviewDto> getCommunityReviews(Long id, int page, int size) {
    //        Pageable pageable = PageRequest.of(page, size);
    //        Page<Review> reviewPage = reviewRepository.findAllByCommunity_Id(id, pageable);
    //        return reviewPage.map(reviewMapper::toDto);
    //    }

    @Override
    public Page<RatingDto> getCommunityRatings(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rating> ratingPage = ratingRepository.findAllByCommunity_Id(id, pageable);
        return ratingPage.map(ratingMapper::toDto);
    }

    @Override
    public RatingDto createCommunityRating(Long id, RatingDto ratingDto, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        Optional<Community> community = communityRepository.findById(id);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
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

        return ratingMapper.toDto(ratingRepository.save(rating));
    }

    @Override
    public RatingAverage getCommunityRatingAverage(Long id) {
        List<Rating> ratingList = ratingRepository.findAllByCommunity_Id(id);
        return new RatingAverage(ratingList);
    }

    @Override
    public void deleteCommunity(long id) {
        // TODO: make sure requesting user has rights to do so

        if (communityRepository.existsById(id)) {
            communityRepository.deleteCommunityById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}
