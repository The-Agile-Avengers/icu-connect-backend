package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * unique username
     */
    @Column(unique = true)
    private String username;

    /**
     * Email of the user. Should not be visible for other users apart from the user itself
     */
    @Column(unique = true)
    private String email;

    /**
     * Encoded password
     */
    private String password;

    /**
     * Communities that the user subscribed
     */
    @ManyToMany()
    private Set<Community> subscriptionSet = new HashSet<>();

    /**
     * Study area of the user
     */
    @ManyToOne()
    private StudyArea studyArea;

    /**
     * Selected avatar (number between 1 - 10)
     */
    private String avatar;
}
