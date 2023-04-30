package com.agileavengers.icuconnectbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agileavengers.icuconnectbackend.model.dto.CommentDto;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.PostDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.service.ICommunityService;

import jakarta.validation.Valid;


import java.util.Optional;

@RestController
@RequestMapping("/communities")
public class CommunityController {

    ICommunityService communityService;

    @Autowired
    CommunityController(ICommunityService communityService) {
        this.communityService = communityService;
    }

    /**
     * Generates example data objects. Will be removed for production
     *
     * @return Community Object
     */
    @PostMapping(value = "/setup")
    public CommunityDto setupExampleCommunity() {
        return communityService.setupExampleCommunity();
    }

    /**
     * Create a new community. Unique module id is required
     *
     * @param communityDto Information about the community.
     * @return The created community
     */
    @PostMapping(value = "")
    public CommunityDto createCommunity(@RequestBody CommunityDto communityDto) {
        return communityService.createCommunity(communityDto);
    }

    /**
     * Pageable request that returns all communities
     *
     * @param page page index
     * @param size number of communities per page
     * @return Page of communities
     */
    @GetMapping(value = "", params = {"page", "size"})
    public Page<CommunityDto> getCommunities(@RequestParam("page") int page,
        @RequestParam("size") int size, @RequestParam("search") Optional<String> search) {
        return communityService.getCommunities(page, size, search);
    }

    /**
     * Get a specific community by id
     *
     * @param moduleId of the community
     * @return Community if it exists
     */
    @GetMapping(value = "/{moduleId}")
    public CommunityDto getCommunity(@PathVariable("moduleId") String moduleId) {
        return communityService.getCommunity(moduleId);
    }

//    /**
//     * Get all reviews linked to a community
//     * @param id id of the community
//     * @param page page index
//     * @param size number of reviews per page
//     * @return Page of reviews
//     */
//    @GetMapping(value = "/{id}/reviews", params = { "page", "size" })
//    public Page<ReviewDto> getCommunityReviews(@PathVariable("id") Long id, @RequestParam("page") int page,
//                                               @RequestParam("size") int size) {
//        return communityService.getCommunityReviews(id, page, size);
//    }

    /**
     * Get all ratings linked to a community
     *
     * @param moduleId of the community
     * @param page     page index
     * @param size     number of ratings per page
     * @return Page of ratings
     */
    @GetMapping(value = "/{moduleId}/ratings", params = {"page", "size"})
    public Page<RatingDto> getCommunityRatings(@PathVariable("moduleId") String moduleId,
                                               @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sortByMostLiked") Optional<Boolean> sortByMostLiked) {
        return communityService.getCommunityRatings(moduleId, page, size, sortByMostLiked);
    }

    @GetMapping(value = "/{moduleId}/ratings/average")
    public RatingAverage getCommunityRatingAverage(@PathVariable("moduleId") String moduleId) {
        return communityService.getCommunityRatingAverage(moduleId);
    }

    @PostMapping(value = "/{moduleId}/ratings")
    public RatingDto rateCommunity(@PathVariable("moduleId") String moduleId, @RequestBody RatingDto ratingDto) {
        // TODO: provide actual username
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createCommunityRating(moduleId, ratingDto, principal.getUsername());
    }

    /**
     * Delete a community.
     *
     * @param moduleId of the community to be deleted
     */
    @DeleteMapping(value = "/{moduleId}")
    public void deleteCommunity(@PathVariable("moduleId") String moduleId) {
        //TODO: only allowed with specific rights. might be removed for production
        communityService.deleteCommunity(moduleId);
    }

    @PostMapping(value = "/{moduleId}/posts")
    public PostDto createPost(@PathVariable("moduleId") String moduleId, @Valid @RequestBody PostDto postDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createPost(moduleId, postDto, principal.getUsername());
    }

    @GetMapping(value = "/{moduleId}/posts", params = { "page", "size" })
    public Page<PostDto> getCommunityPosts(@PathVariable("moduleId") String moduleId, @RequestParam("page") int page,
            @RequestParam("size") int size, @RequestParam("year") Optional<Integer> year) {
        return communityService.getCommunityPosts(moduleId, page, size, year);
    }

    @DeleteMapping(value = "/{moduleId}/posts/{postId}")
    public void deletePost(@PathVariable String moduleId, @PathVariable Long postId) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        communityService.deletePost(moduleId, postId, principal.getUsername());
    }

    @PostMapping(value = "/{moduleId}/posts/{postId}/comments")
    public CommentDto createPostComment(@PathVariable String moduleId, @PathVariable Long postId,
        @Valid @RequestBody CommentDto commentDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createComment(moduleId, postId, commentDto, principal.getUsername());
    }

    @PostMapping(value = "/{moduleId}/ratings/{ratingId}/thumbsUp")
    public RatingDto thumbsUp(@PathVariable("moduleId") String moduleId, @PathVariable("ratingId") Long ratingId, @RequestBody RatingDto ratingDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.thumbsUp(moduleId, ratingId, principal.getUsername());
    }
}
