package com.agileavengers.icuconnectbackend.controller;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
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


    @GetMapping(value = "")
    UserDetailDto getUser() {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(principal.getUsername());
    }

    @PutMapping(value = "")
    UserDetailDto updateUser(@RequestBody UserDetailDto userDetailDto) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateUser(principal.getUsername(), userDetailDto);
    }

    @PutMapping(value = "/communities/{moduleId}")
    Set<CommunityDto> updateCommunityRelation(@PathVariable(value = "moduleId") String moduleId) {
        UserDetails principal =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateCommunityRelation(principal.getUsername(), moduleId);

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
