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

    private String moduleId;

    private String name;

    @ManyToOne
    private Instructor instructor;


}