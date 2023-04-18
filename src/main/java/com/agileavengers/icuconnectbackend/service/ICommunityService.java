package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommentDto;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICommunityService {
    CommunityDto setupExampleCommunity();

    CommunityDto createCommunity(CommunityDto communityDto);

    Page<CommunityDto> getCommunities(int page, int size, Optional<String> search);

    CommunityDto getCommunity(String moduleId);

    Page<RatingDto> getCommunityRatings(String moduleId, int page, int size, Optional<Boolean> sortByMostLiked);

    RatingDto thumbsUp(String moduleId, Long ratingId, String username);

    RatingDto createCommunityRating(String moduleId, RatingDto ratingDto, String username);

    RatingAverage getCommunityRatingAverage(String moduleId);

    void deleteCommunity(String moduleId);
    
    PostDto createPost(String moduleId, PostDto postDto, String username);

    Page<PostDto> getCommunityPosts(String moduleId, int pageNumber, int size);

    void deletePost(String moduleId, Long postId, String username);

    CommentDto createComment(String moduleId, Long postId, CommentDto commentDto, String username);
}
