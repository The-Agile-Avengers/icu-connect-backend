package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    /**
     * Instructor name, format preferably "Title Firstname Lastname"
     */
    @Column(unique = true)
    private String name;

}
