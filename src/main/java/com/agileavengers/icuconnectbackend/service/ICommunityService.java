package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import org.springframework.data.domain.Page;

public interface ICommunityService {
    CommunityDto setupExampleCommunity();

    CommunityDto createCommunity(CommunityDto communityDto);

    Page<CommunityDto> getCommunities(int page, int size);

    CommunityDto getCommunity(Long id);

    //    Page<ReviewDto> getCommunityReviews(Long id, int page, int size);
    Page<RatingDto> getCommunityRatings(Long id, int page, int size);

    RatingDto createCommunityRating(Long id, RatingDto ratingDto, String username);

    RatingAverage getCommunityRatingAverage(Long id);

    void deleteCommunity(long id);
}
