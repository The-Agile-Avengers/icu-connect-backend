package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingAverage;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.ReviewDto;
import com.agileavengers.icuconnectbackend.service.ICommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
     * @return Community Object
     */
    @PostMapping(value = "/setup")
    public CommunityDto setupExampleCommunity() {
        return communityService.setupExampleCommunity();
    }

    /**
     * Create a new community. Unique module id is required
     * @param communityDto Information about the community.
     * @return The created community
     */
    @PostMapping(value = "")
    public CommunityDto createCommunity(@RequestBody CommunityDto communityDto) {
        return communityService.createCommunity(communityDto);
    }

    /**
     * Pageable request that returns all communities
     * @param page page index
     * @param size number of communities per page
     * @return Page of communities
     */
    @GetMapping(value = "", params = { "page", "size" })
    public Page<CommunityDto> getCommunities(@RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        return communityService.getCommunities(page, size);
    }

    /**
     * Get a specific community by id
     * @param id id of the community
     * @return Community if it exists
     */
    @GetMapping(value = "/{id}")
    public CommunityDto getCommunity(@PathVariable("id") Long id) {
        return communityService.getCommunity(id);
    }

    /**
     * Get all reviews linked to a community
     * @param id id of the community
     * @param page page index
     * @param size number of reviews per page
     * @return Page of reviews
     */
    @GetMapping(value = "/{id}/reviews", params = { "page", "size" })
    public Page<ReviewDto> getCommunityReviews(@PathVariable("id") Long id, @RequestParam("page") int page,
                                               @RequestParam("size") int size) {
        return communityService.getCommunityReviews(id, page, size);
    }

    @GetMapping(value = "/{id}/ratings", params = { "page", "size" })
    public Page<RatingDto> getCommunityRatings(@PathVariable("id") Long id, @RequestParam("page") int page,
                                               @RequestParam("size") int size) {
        return communityService.getCommunityRatings(id, page, size);
    }

    @GetMapping(value = "/{id}/ratings/average")
    public RatingAverage getCommunityRatingAverage(@PathVariable("id") Long id) {
        return communityService.getCommunityRatingAverage(id);
    }

    @PostMapping(value = "/{id}/ratings")
    public RatingDto rateCommunity(@PathVariable("id") Long id, @RequestBody RatingDto ratingDto) {
        // TODO: provide actual username
        return communityService.createCommunityRating(id, ratingDto, "");
    }

    /**
     * Delete a community.
     * @param id id of the community to be deleted
     */
    @DeleteMapping(value = "/{id}")
    public void deleteCommunity(@PathVariable("id") Long id) {
        //TODO: only allowed with specific rights. might be removed for production
        communityService.deleteCommunity(id);
    }
}
