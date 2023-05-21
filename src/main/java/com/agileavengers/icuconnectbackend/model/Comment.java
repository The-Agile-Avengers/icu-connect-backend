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

    /**
     * User that created the comment
     */
    @ManyToOne
    private User creator;

    /**
     * Time when the comment was posted
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp creation;

    /**
     * Post that the comment belongs to
     */
    @ManyToOne
    private Post post;

    /**
     * Content of the comment
     */
    private String text;

}
