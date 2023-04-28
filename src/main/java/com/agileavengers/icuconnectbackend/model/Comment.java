package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User creator;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp creation;

    @ManyToOne
    private Post post;

    private String text;

}
