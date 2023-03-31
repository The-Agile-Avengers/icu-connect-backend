package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final CommunityMapper communityMapper;
    private final RatingMapper ratingMapper;

    CommunityRepository communityRepository;
    UserRepository userRepository;
    RatingRepository ratingRepository;



    @Autowired
    public UserService(CommunityMapper communityMapper, RatingMapper ratingMapper, CommunityRepository communityRepository, UserRepository userRepository, RatingRepository ratingRepository) {
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Set<CommunityDto> updateCommunityRelation(String username, String moduleId) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        Optional<Community> optCommunity = communityRepository.findCommunityByModuleId(moduleId);
        if (optCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Community does not exist");
        }
        Community community = optCommunity.get();

        if (user.getSubscriptionSet().contains(community)) {
            user.getSubscriptionSet().remove(community);
        } else {
            user.getSubscriptionSet().add(community);
        }

        user = userRepository.save(user);


        return user.getSubscriptionSet().stream().map(communityMapper::toDto).collect(Collectors.toSet());

    }

    @Override
    public Set<CommunityDto> getJoinedCommunities(String username) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        return user.getSubscriptionSet().stream().map(communityMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public RatingDto getCommunityRating(String username, String moduleId) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        Optional<Community> optCommunity = communityRepository.findCommunityByModuleId(moduleId);
        if (optCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Community does not exist");
        }
        Optional<Rating> rating = ratingRepository.findByCommunity_ModuleIdAndCreator_Id(moduleId, user.getId());
        return rating.map(ratingMapper::toDto).orElse(null);
    }

    @Override
    public RatingDto getCommunityRating(String username, String moduleId) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        Optional<Community> optCommunity = communityRepository.findCommunityByModuleId(moduleId);
        if (optCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Community does not exist");
        }
        Optional<Rating> rating = ratingRepository.findByCommunity_ModuleIdAndCreator_Id(moduleId, user.getId());
        return rating.map(ratingMapper::toDto).orElse(null);
    }
}
