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
public class Post {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    /**
     * User that created the post
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    /**
     * The community that the post was created about
     */
    @ManyToOne
    private Community community;

    /**
     * Time when the post was created
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp creation;

    /**
     * Title of the post
     */
    private String title;

    /**
     * Content of the post
     */
    private String text;

    /**
     * Possible link to MongoDB
     */
    private String documentId;

}
