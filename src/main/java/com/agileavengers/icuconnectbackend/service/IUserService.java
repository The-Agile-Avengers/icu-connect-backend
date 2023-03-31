package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;

import java.util.Set;

public interface IUserService {
    void updateCommunityRelation(String username, String moduleId);
    Set<CommunityDto> getJoinedCommunities(String username);
    RatingDto getCommunityRating(String username, String moduleId);
}
