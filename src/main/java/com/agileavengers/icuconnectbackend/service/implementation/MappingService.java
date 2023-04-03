package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    UserRepository userRepository;
    RatingRepository ratingRepository;

    @Autowired
    public MappingService(UserRepository userRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    public Integer subscriberCount(Community community) {
        return userRepository.countAllBySubscriptionListContaining(community);
    }

    public RatingAverage calculateRating(Community community) {
        return new RatingAverage(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId()));
    }

    public Integer getRatingThumbsUp(Rating rating) {
        return rating.getThumbsUp() != null ? rating.getThumbsUp().size() : 0;
    }

    public Boolean isJoined(Community community) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername()).get();
        if (user.getSubscriptionList() == null) {
            return false;
        }
        return user.getSubscriptionList().contains(community);
    }
}
