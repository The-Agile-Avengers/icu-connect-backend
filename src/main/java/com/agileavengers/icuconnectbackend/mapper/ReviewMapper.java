package com.agileavengers.icuconnectbackend.mapper;

import com.agileavengers.icuconnectbackend.model.Review;
import com.agileavengers.icuconnectbackend.model.dto.ReviewDto;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring"
)
public abstract class ReviewMapper {

    @Autowired
    protected MappingService mappingService;

    @Mapping(target = "thumbsUp", expression = "java(mappingService.getReviewThumbsUp(review))")
    public abstract ReviewDto toDto(Review review);
    @Mapping(target = "thumbsUp", expression = "java(null)")
    public abstract Review fromDto(ReviewDto reviewDto);
}
