package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
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

    CommunityRepository communityRepository;
    UserRepository userRepository;



    @Autowired
    public UserService(CommunityMapper communityMapper, CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityMapper = communityMapper;
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void updateCommunityRelation(String username, Long communityId) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        Optional<Community> optCommunity = communityRepository.findCommunityById(communityId);
        if (optCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Community does not exist");
        }
        Community community = optCommunity.get();

        if (user.getSubscriptionList().contains(community)) {
            user.getSubscriptionList().remove(community);
        } else {
            user.getSubscriptionList().add(community);
        }

        userRepository.save(user);




    }

    @Override
    public Set<CommunityDto> getJoinedCommunities(String username) {
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }
        User user = optUser.get();
        return user.getSubscriptionList().stream().map(communityMapper::toDto).collect(Collectors.toSet());
    }
}
