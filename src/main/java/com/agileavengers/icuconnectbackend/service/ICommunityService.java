package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

public interface ICommunityService {
    CommunityDto setupExampleCommunity();

    CommunityDto createCommunity(CommunityDto communityDto);

    Page<CommunityDto> getCommunities(int page, int size);

    CommunityDto getCommunity(String moduleId);

    Page<RatingDto> getCommunityRatings(String moduleId, int page, int size);

    RatingDto createCommunityRating(String moduleId, RatingDto ratingDto, String username);

    RatingAverage getCommunityRatingAverage(String moduleId);

    void deleteCommunity(String moduleId);
    
    PostDto createPost(String moduleId, PostDto postDto, UserDetails userDetails);

    Page<PostDto> getCommunityPosts(String moduleId, int pageNumber, int size);
}
