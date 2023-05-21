package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import com.agileavengers.icuconnectbackend.service.IStudyAreaService;
import com.agileavengers.icuconnectbackend.service.IUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    IUserService userService;
    IStudyAreaService studyAreaService;

    public UserController(IUserService userService, IStudyAreaService studyAreaService) {
        this.userService = userService;
        this.studyAreaService = studyAreaService;
    }


    /**
     * Endpoint for a logged in user to query their own details.
     * @return detailed user object
     */
    @GetMapping(value = "")
    UserDetailDto getUser() {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(principal.getUsername());
    }

    /**
     * Endpoint to update the detailed user information. Email and username change only possible if not already taken.
     *
     * @param userDetailDto details about which attributes to update
     * @return updated and persisted information
     */
    @PutMapping(value = "")
    UserDetailDto updateUser(@RequestBody UserDetailDto userDetailDto) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateUser(principal.getUsername(), userDetailDto);
    }

    /**
     * Endpoint to subscribe or unsubscribe a community
     * @param moduleId id of the community to change the relation
     * @return set of subscribed communities
     */
    @PutMapping(value = "/communities/{moduleId}")
    Set<CommunityDto> updateCommunityRelation(@PathVariable(value = "moduleId") String moduleId) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateCommunityRelation(principal.getUsername(), moduleId);

    }

    /**
     * Endpoint to query all currently subscribed communities
     * @return set of all subscribed communities
     */
    @GetMapping(value = "/communities")
    Set<CommunityDto> getJoinedCommunities() {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getJoinedCommunities(principal.getUsername());
    }

    /**
     * Endpoint to get the rating the logged in user created for a community, if it exists. Else it will return null.
     * @param moduleId of the relevant community
     * @return rating object or null
     */
    @GetMapping(value = "/communities/{moduleId}/ratings")
    RatingDto getCommunityRating(@PathVariable("moduleId") String moduleId) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getCommunityRating(principal.getUsername(), moduleId);
    }
}
