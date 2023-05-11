package com.agileavengers.icuconnectbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

/**
 * Controller to handle all functionality related to communities, including all entities that are directly related to a community, such as ratings and posts.
 */

@RestController
@RequestMapping("/communities")
public class CommunityController {

    ICommunityService communityService;

    @Autowired
    CommunityController(ICommunityService communityService) {
        this.communityService = communityService;
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

    /**
     * Calculates the rating average of a specific community.
     *
     * @param moduleId Id of community
     * @return Calculated average rating
     */
    @GetMapping(value = "/{moduleId}/ratings/average")
    public RatingAverage getCommunityRatingAverage(@PathVariable("moduleId") String moduleId) {
        return communityService.getCommunityRatingAverage(moduleId);
    }

    /**
     * Creates a new rating object related to a community. Each user is only allowed to rate a community once. If it is already rated it will cause a BAD_REQEST.
     *
     * @param moduleId ID of rated community
     * @param ratingDto object with information about rating
     * @return saved rating
     */
    @PostMapping(value = "/{moduleId}/ratings")
    public RatingDto rateCommunity(@PathVariable("moduleId") String moduleId, @RequestBody RatingDto ratingDto) {
        // TODO: provide actual username
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createCommunityRating(moduleId, ratingDto, principal.getUsername());
    }

    /**
     * Create a new post related to a community.
     * @param moduleId Id of the community to create a new post about
     * @param postDto details about the post
     * @return persisted post object
     */

    @PostMapping(value = "/{moduleId}/posts")
    public PostDto createPost(@PathVariable("moduleId") String moduleId, @Valid @RequestBody PostDto postDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createPost(moduleId, postDto, principal.getUsername());
    }

    /**
     * Gets paged posts of a community.
     *
     * @param moduleId id of the community
     * @param page which page to return
     * @param size how many elements per page
     * @param year if given, filters the posts to only be from that year
     * @return page of posts
     */

    @GetMapping(value = "/{moduleId}/posts", params = { "page", "size" })
    public Page<PostDto> getCommunityPosts(@PathVariable("moduleId") String moduleId, @RequestParam("page") int page,
            @RequestParam("size") int size, @RequestParam("year") Optional<Integer> year) {
        return communityService.getCommunityPosts(moduleId, page, size, year);
    }

    /**
     * Allows the creator of a post to delete the post.
     *
     * @param moduleId Community that the post relates to
     * @param postId id of the post to be deleted
     */
    @DeleteMapping(value = "/{moduleId}/posts/{postId}")
    public void deletePost(@PathVariable String moduleId, @PathVariable Long postId) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        communityService.deletePost(moduleId, postId, principal.getUsername());
    }

    /**
     * Endpoint to add a comment to a post.
     * Will throw BAD_REQUEST if the community or the post does not exist.
     *
     * @param moduleId id the community
     * @param postId post related to the community
     * @param commentDto object containing information about the comment
     * @return created comment object
     */
    @PostMapping(value = "/{moduleId}/posts/{postId}/comments")
    public CommentDto createPostComment(@PathVariable String moduleId, @PathVariable Long postId,
        @Valid @RequestBody CommentDto commentDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.createComment(moduleId, postId, commentDto, principal.getUsername());
    }

    /**
     * Endpoint to like a rating related to a community. If already liked it will unlike the rating.
     * Will return BAD_REQUEST if community or rating does not exist.
     *
     * @param moduleId Community that belongs to rating
     * @param ratingId Rating that will be liked or unliked
     * @return Rating object with current thumbsUp property
     */
    @PostMapping(value = "/{moduleId}/ratings/{ratingId}/thumbsUp")
    public RatingDto thumbsUp(@PathVariable("moduleId") String moduleId, @PathVariable("ratingId") Long ratingId) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return communityService.thumbsUp(moduleId, ratingId, principal.getUsername());
    }
}
