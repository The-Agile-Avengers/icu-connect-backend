package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.Community;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommunityRepository extends PagingAndSortingRepository<Community, Long> {
}
