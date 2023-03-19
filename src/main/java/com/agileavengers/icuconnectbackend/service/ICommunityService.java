package com.agileavengers.icuconnectbackend.service;

import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.model.dto.ReviewDto;
import org.springframework.data.domain.Page;

public interface ICommunityService {
    CommunityDto setupExampleCommunity();
    CommunityDto createCommunity(CommunityDto communityDto);
    Page<CommunityDto> getCommunities(int page, int size);
    CommunityDto getCommunity(Long id);
    Page<ReviewDto> getCommunityReviews(Long id, int page, int size);
    void deleteCommunity(long id);
}
