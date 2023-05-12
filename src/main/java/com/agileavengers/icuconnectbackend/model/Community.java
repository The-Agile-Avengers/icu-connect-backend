package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    /**
     * unique module id
     */
    @Column(unique = true)
    private String moduleId;

    /**
     * Name of the module
     */
    private String name;

    /**
     * How many ECTS the course has
     */
    private Double ects;

    /**
     * The instructor currently responsible for the course
     */
    @ManyToOne
    private Instructor instructor;


}
