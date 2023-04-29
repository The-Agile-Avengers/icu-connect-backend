package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.config.JwtTokenUtil;
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
import com.agileavengers.icuconnectbackend.service.IUserService;
import com.agileavengers.icuconnectbackend.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    JwtUserDetailsService userDetailsService;
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserService(CommunityMapper communityMapper, RatingMapper ratingMapper, UserMapper userMapper, CommunityRepository communityRepository, UserRepository userRepository, RatingRepository ratingRepository, JwtUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.communityMapper = communityMapper;
        this.ratingMapper = ratingMapper;
        this.userMapper = userMapper;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public UserDetailDto getUser(String username) {
        User user = getUserFromDb(username);
        return userMapper.toDetailedDto(user);
    }

    @Override
    public ResponseEntity<UserDetailDto> updateUser(String username, UserDetailDto userDetailDto) {
        User user = getUserFromDb(username);
        HttpHeaders responseHeaders = new HttpHeaders();
        if (userDetailDto.getUsername() != null) {
            if (userRepository.findByUsername(userDetailDto.getUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is taken.");
            }
            user.setUsername(userDetailDto.getUsername());
        }
        if (userDetailDto.getEmail() != null) {
            if (userRepository.findByEmail(userDetailDto.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is taken.");
            }
            user.setEmail(userDetailDto.getEmail());
        }
        if (userDetailDto.getStudyArea() != null) {
            user.setStudyArea(userDetailDto.getStudyArea());
        }
        if (userDetailDto.getAvatar() != null) {
            user.setAvatar(userDetailDto.getAvatar());
        }
        user = userRepository.save(user);
        // has to be after save because userDetailsService makes query on db
        if (userDetailDto.getUsername() != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDetailDto.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);
            responseHeaders.set("token", token);
        }
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(userMapper.toDetailedDto(user));
    }

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

    @Override
    public Set<CommunityDto> getJoinedCommunities(String username) {
        User user = getUserFromDb(username);
        return user.getSubscriptionSet().stream().map(communityMapper::toDto).collect(Collectors.toSet());
    }

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


    private User getUserFromDb(String username) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        return optUser.get();
    }

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
            user.setStudyArea(update.getStudyArea());
        }
        if (update.getAvatar() != null) {
            user.setAvatar(update.getAvatar());
        }
        return user;
    }
}
