package com.agileavengers.icuconnectbackend.service.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.User;

public class ServiceHelper {
    public void isJoined(User user, Community community) throws ResponseStatusException {
        if(!user.getSubscriptionSet().contains(community)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not joined the community");
        }
    }
}
