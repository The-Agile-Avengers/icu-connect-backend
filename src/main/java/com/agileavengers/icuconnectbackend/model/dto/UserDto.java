package com.agileavengers.icuconnectbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO of a general user. Used for general endpoints, informaiton visible for other users too.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    /**
     * Username of the user
     */
    private String username;

    /**
     * selected avatar (value between 1-10)
     */
    private String avatar;
}
