package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = MappingService.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class RatingMapper {


    public MappingService mappingService;

    @Autowired
    public void setMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
    }


    @Mapping(target = "thumbsUp", expression = "java(mappingService.getRatingThumbsUp(rating))")
    public abstract RatingDto toDto(Rating rating);

    @Mapping(target = "creator", expression = "java(null)")
    @Mapping(target = "thumbsUp", expression = "java(null)")
    public abstract Rating fromDto(RatingDto ratingDto);
}
