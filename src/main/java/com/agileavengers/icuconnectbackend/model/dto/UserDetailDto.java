package com.agileavengers.icuconnectbackend.model.dto;

import com.agileavengers.icuconnectbackend.model.StudyArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO only for endpoints the user itself queries. Information is only visible for the user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * Email of the user
     */
    private String email;

    /**
     * Study area of the user
     */
    private StudyArea studyArea;

    /**
     * Selected avatar (value between 1 - 10)
     */
    private String avatar;
}
