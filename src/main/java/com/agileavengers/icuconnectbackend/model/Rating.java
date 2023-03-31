package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User creator;

    @ManyToOne
    private Community community;
    private Double teaching;
    private Double content;
    private Double workload;

    private String text;

    @ManyToMany
    private Set<User> thumbsUp;
}
