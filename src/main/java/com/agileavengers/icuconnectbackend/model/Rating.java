package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.sql.Timestamp;
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

    /**
     * user that created the rating
     */
    @ManyToOne
    private User creator;
    /**
     * Time when the rating was created
     */
    private Timestamp creation;

    /**
     * The community that is rated
     */
    @ManyToOne
    private Community community;
    /**
     * Score for the teaching style between 0-5
     */
    private Double teaching;
    /**
     * Score for the lecture content between 0-5
     */
    private Double content;
    /**
     * Score for the required workload between 0-5
     */
    private Double workload;

    /**
     * Optional text explaining the given scores
     */
    private String text;

    /**
     * Users that deemed the rating as useful
     */
    @ManyToMany
    private Set<User> thumbsUp;

    /**
     * Count of Users that deemed the rating as useful
     */
    @Formula("(SELECT COUNT(*) FROM rating_thumbs_up as obj WHERE obj.rating_id = id)")
    private Integer thumbsUpCount;

    /**
     * Method to update if the user has liked the rating or not
     * @param user that wants to change the status of their like
     * @return updated rating
     */
    public Rating modifyThumbsUp(User user) {
        if (this.thumbsUp.contains(user)) {
            this.thumbsUp.remove(user);
        } else {
            this.thumbsUp.add(user);
        }
        return this;
    }
}
