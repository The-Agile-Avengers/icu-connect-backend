package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.dto.CommunityDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring"
)
public abstract class CommunityMapper {

    @Autowired
    protected MappingService mappingService;

    @Mapping(target = "subscribersCount", expression = "java(mappingService.subscriberCount(community))")
    @Mapping(target = "rating", expression = "java(mappingService.calculateRating(community))")
    public abstract CommunityDto toDto(Community community);
    public abstract Community fromDto(CommunityDto communityDto);
}
