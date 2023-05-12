package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
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
    private final UserMapper userMapper;

    CommunityRepository communityRepository;
    UserRepository userRepository;
    RatingRepository ratingRepository;

    IStudyAreaService studyAreaService;



    @Autowired
    public UserService(CommunityMapper communityMapper, RatingMapper ratingMapper, UserMapper userMapper, CommunityRepository communityRepository, UserRepository userRepository, RatingRepository ratingRepository, IStudyAreaService studyAreaService) {
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.userMapper = userMapper;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.studyAreaService = studyAreaService;
    }

    /**
     * Get user having username from db and map to DTO. This method maps to a DTO containing more than just standard information and is only  meant for the user
     * @param username of the relevant user
     * @return detailed user information
     */
    @Override
    public UserDetailDto getUser(String username) {
        User user = getUserFromDb(username);
        return userMapper.toDetailedDto(user);
    }

    /**
     * Update the details of the logged in user. If the new username or the new email is already taken an exception is thrown.
     * @param username of the relevant user
     * @param userDetailDto information to change the user to
     * @return changed and persisted user details
     */
    @Override
    public UserDetailDto updateUser(String username, UserDetailDto userDetailDto) {
        User user = getUserFromDb(username);
        user = this.updateFields(user, userDetailDto);
        user = userRepository.save(user);
        return userMapper.toDetailedDto(user);
    }

    /**
     * Change the subscription status of the logged in user. If already subscribed it will unsubscribe.
     * @param username of the relevant user
     * @param moduleId of the community that will be (un-)subscribed
     * @return set of all subscribed communities
     */
    @Override
    public Set<CommunityDto> updateCommunityRelation(String username, String moduleId) {
        User user = getUserFromDb(username);
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

    /**
     * Get the set of the currently subscribed communities of the logged in user
     * @param username of the relevant user
     * @return set of subscribed communities
     */
    @Override
    public Set<CommunityDto> getJoinedCommunities(String username) {
        User user = getUserFromDb(username);
        return user.getSubscriptionSet().stream().map(communityMapper::toDto).collect(Collectors.toSet());
    }

    /**
     * Get the rating the user created for a community. If the user has not rated yet, it will return null.
     * @param username of the logged in user
     * @param moduleId of the relevant community
     * @return rating created by the user or null
     */
    @Override
    public RatingDto getCommunityRating(String username, String moduleId) {
        User user = getUserFromDb(username);
        Optional<Community> optCommunity = communityRepository.findCommunityByModuleId(moduleId);
        if (optCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Community does not exist");
        }
        Optional<Rating> rating = ratingRepository.findByCommunity_ModuleIdAndCreator_Id(moduleId, user.getId());
        return rating.map(ratingMapper::toDto).orElse(null);
    }

    /**
     * Check if a username exists in the database and return it or throw an exception.
     *
     * @param username of the relevant user
     * @return user object
     */
    private User getUserFromDb(String username) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        return optUser.get();
    }

    /**
     * Update the fields of a user according to the input. throw an exception if unique constraints of username and email are not fulfilled
     * @param user object to be updated
     * @param update details about which fields should be updated.
     * @return updated user object
     */
    private User updateFields(User user, UserDetailDto update) {
        if (update.getUsername() != null) {
            if (userRepository.findByUsername(update.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
            }
            user.setUsername(update.getUsername());
        }
        if (update.getEmail() != null) {
            if (userRepository.findByEmail(update.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
            }
            user.setEmail(update.getEmail());
        }
        if (update.getStudyArea() != null) {
            user.setStudyArea(studyAreaService.getOrCreateStudyArea(update.getStudyArea()));
        }
        if (update.getAvatar() != null) {
            user.setAvatar(update.getAvatar());
        }
        return user;
    }
}
