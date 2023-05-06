package com.agileavengers.icuconnectbackend.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column(unique = true)
    private String moduleId;

    private String name;

    private Double ects;

    @ManyToOne
    private Instructor instructor;

    @OneToMany
    private Set<File> uploadedFiles = new HashSet<>();
}
