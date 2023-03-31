package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.service.IUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PutMapping(value = "/communities/{moduleId}")
    void updateCommunityRelation(@PathVariable(value = "moduleId") String moduleId) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateCommunityRelation(principal.getUsername(), moduleId);

    }

    @GetMapping(value = "/communities")
    Set<CommunityDto> getJoinedCommunities() {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getJoinedCommunities(principal.getUsername());
    }

    @GetMapping(value = "/communities/{moduleId}/ratings")
    RatingDto getCommunityRating(@PathVariable("moduleId") String moduleId) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getCommunityRating(principal.getUsername(), moduleId);
    }
}
