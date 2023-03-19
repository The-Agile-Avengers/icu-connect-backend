package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.ReviewMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.Review;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.ReviewDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.repository.ReviewRepository;
import com.agileavengers.icuconnectbackend.service.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CommunityService implements ICommunityService {

    CommunityRepository communityRepository;

    InstructorRepository instructorRepository;

    ReviewRepository reviewRepository;

    private final CommunityMapper communityMapper;
    private final ReviewMapper reviewMapper;
    private final InstructorMapper instructorMapper;



    @Autowired
    CommunityService(CommunityRepository communityRepository,
                     InstructorRepository instructorRepository,
                     ReviewRepository reviewRepository,
                     CommunityMapper communityMapper,
                     ReviewMapper reviewMapper, InstructorMapper instructorMapper) {
        this.communityRepository = communityRepository;
        this.instructorRepository = instructorRepository;
        this.reviewRepository = reviewRepository;
        this.communityMapper = communityMapper;
        this.reviewMapper = reviewMapper;
        this.instructorMapper = instructorMapper;
    }

    @Override
    public CommunityDto setupExampleCommunity() {
        Instructor instructor = Instructor.builder().name("Anna King").build();
        instructor = instructorRepository.save(instructor);

        Community community = Community.builder().name("Lecture 1").moduleId("UZH123").instructor(instructor).build();
        community = communityRepository.save(community);

        return communityMapper.toDto(community);
    }

    @Override
    public CommunityDto createCommunity(CommunityDto communityDto) {
        Community community = new Community();
        if (communityDto.getModuleId() != null) {
            if (communityRepository.findCommunityByModuleId(communityDto.getModuleId()).isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Module ID already exists"
                );
            }
            community.setModuleId(communityDto.getModuleId());
        }
        if (communityDto.getInstructor() != null) {
            Optional<Instructor> instructor = instructorRepository.findInstructorByName(communityDto.getName());
            if (instructor.isPresent()) {
                community.setInstructor(instructor.get());
            } else {
                community.setInstructor(instructorRepository.save(instructorMapper.fromDto(communityDto.getInstructor())));
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
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }

    @Override
    public Page<ReviewDto> getCommunityReviews(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAllByCommunity_Id(id, pageable);
        return reviewPage.map(reviewMapper::toDto);
    }

    @Override
    public void deleteCommunity(long id) {
        // TODO: make sure requesting user has rights to do so

        if (communityRepository.existsById(id)) {
            communityRepository.deleteCommunityById(id);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
    }
}
