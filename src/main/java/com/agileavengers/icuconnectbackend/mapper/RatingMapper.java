package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Rating;
import com.agileavengers.icuconnectbackend.model.dto.RatingDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring"
)
public abstract class RatingMapper {

    @Autowired
    protected MappingService mappingService;

    public abstract RatingDto toDto(Rating rating);

    @Mapping(target = "creator", expression = "java(null)")
    public abstract Rating fromDto(RatingDto ratingDto);
}
