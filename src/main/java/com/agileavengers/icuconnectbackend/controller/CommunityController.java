package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
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

    @PostMapping(value = "/setup")
    public CommunityDto setupExampleCommunity() {
        return communityService.setupExampleCommunity();
    }

    @PostMapping(value = "")
    public CommunityDto createCommunity(@RequestBody CommunityDto communityDto) {
        return communityService.createCommunity(communityDto);
    }

    @GetMapping(value = "", params = { "page", "size" })
    public Page<CommunityDto> getCommunities(@RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        return communityService.getCommunities(page, size);
    }

    @GetMapping(value = "/{id}")
    public CommunityDto getCommunity(@PathVariable("id") Long id) {
        return communityService.getCommunity(id);
    }

    @GetMapping(value = "/{id}/reviews", params = { "page", "size" })
    public Page<ReviewDto> getCommunityReviews(@PathVariable("id") Long id, @RequestParam("page") int page,
                                               @RequestParam("size") int size) {
        return communityService.getCommunityReviews(id, page, size);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCommunity(@PathVariable("id") Long id) {
        communityService.deleteCommunity(id);
    }
}
