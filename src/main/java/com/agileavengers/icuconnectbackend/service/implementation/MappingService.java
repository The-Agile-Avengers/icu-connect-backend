package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.File;
import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class MappingService {

    UserRepository userRepository;
    RatingRepository ratingRepository;

    @Autowired
    public MappingService(UserRepository userRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Counts how many subscribers the community currently has
     * @param community object containing the relevant information
     * @return counted subscribers
     */
    public Integer subscriberCount(Community community) {
        return userRepository.countAllBysubscriptionSetContaining(community);
    }

    /**
     * Calculates the average ratings of a community
     * @param community object containing the relevant information
     * @return calculated average
     */
    public RatingAverage calculateRating(Community community) {
        return new RatingAverage(ratingRepository.findAllByCommunity_ModuleId(community.getModuleId()));
    }

    /**
     * Calculate how many users have liked a rating
     * @param rating relevant rating object
     * @return counted thumbs up
     */
    public Integer getRatingThumbsUp(Rating rating) {
        return rating.getThumbsUp() != null ? rating.getThumbsUp().size() : 0;
    }

    /**
     * Check if the logged in user is currently subscribed to a community
     * @param community relevant community
     * @return whether user is subscribed or not
     */
    public Boolean isJoined(Community community) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByUsername(principal.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        User user = userOptional.get();
        if (user.getSubscriptionSet() == null) {
            return false;
        }
        return user.getSubscriptionSet().contains(community);
    }

    /**
     * Check if the logged in user has liked a rating object
     * @param rating relevant rating object
     * @return whether the user has liked the rating or not
     */
    public Boolean getHasLiked(Rating rating) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findByUsername(principal.getUsername());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }

        if (!rating.getThumbsUp().contains(user.get())) {
            return false;
        } 

        return true;
    }

    public Boolean getHasUploaded(File file) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findByUsername(principal.getUsername());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }
        return file.getCreator().equals(user.get());
    }
}
