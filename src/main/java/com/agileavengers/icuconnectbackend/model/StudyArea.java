package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyArea {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    /**
     * Name of the study area
     */
    @Column(unique = true)
    private String name;
}
