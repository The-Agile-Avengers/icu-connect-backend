package com.agileavengers.icuconnectbackend.service.implementation;

import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
class MappingServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RatingRepository ratingRepository;


    @InjectMocks
    MappingService mappingService;

    @Test
    void subscriberCount() {
        mappingService.calculateRating(null);
    }
}