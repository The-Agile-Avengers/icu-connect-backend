package com.agileavengers.icuconnectbackend.model.dto;

import com.agileavengers.icuconnectbackend.model.StudyArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDto {
    private Long id;

    private String username;

    private String email;

    private StudyArea studyArea;

    private String avatar;
}
