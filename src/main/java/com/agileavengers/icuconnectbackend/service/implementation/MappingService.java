package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.Review;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.ReviewRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    UserRepository userRepository;
    RatingRepository ratingRepository;
    ReviewRepository reviewRepository;

    @Autowired
    MappingService(UserRepository userRepository, RatingRepository ratingRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
    }

    // TODO: Implement subscriber count
    public Integer subscriberCount(Community community)  {
        return 10;
    }

    // TODO: Implement rating calculation
    public RatingAverage calculateRating(Community community) {
        return new RatingAverage(ratingRepository.findAllByCommunity_Id(community.getId()));
    }

    public Integer getReviewThumbsUp(Review review) {
        return review.getThumbsUp().size();
    }
}
