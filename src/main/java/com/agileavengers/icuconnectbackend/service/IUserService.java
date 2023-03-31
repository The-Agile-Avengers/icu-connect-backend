package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;

import java.util.Set;

public interface IUserService {
    void updateCommunityRelation(String username, Long communityId);
    Set<CommunityDto> getJoinedCommunities(String username);
}
