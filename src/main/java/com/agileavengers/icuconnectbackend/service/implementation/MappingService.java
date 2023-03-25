package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // TODO: Implement subscriber count
    public Integer subscriberCount(Community community) {
        return userRepository.countAllBySubscriptionListContaining(community);
    }

    // TODO: Implement rating calculation
    public RatingAverage calculateRating(Community community) {
        return new RatingAverage(ratingRepository.findAllByCommunity_Id(community.getId()));
    }

    public Integer getRatingThumbsUp(Rating rating) {
        return rating.getThumbsUp() != null ? rating.getThumbsUp().size() : 0;
    }
}
