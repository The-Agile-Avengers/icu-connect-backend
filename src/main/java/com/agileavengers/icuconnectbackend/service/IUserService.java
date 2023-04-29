package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.model.dto.UserDetailDto;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface IUserService {
    UserDetailDto getUser(String username);

    ResponseEntity<UserDetailDto> updateUser(String username, UserDetailDto userDetailDto);

    Set<CommunityDto> updateCommunityRelation(String username, String moduleId);

    Set<CommunityDto> getJoinedCommunities(String username);

    RatingDto getCommunityRating(String username, String moduleId);
}
