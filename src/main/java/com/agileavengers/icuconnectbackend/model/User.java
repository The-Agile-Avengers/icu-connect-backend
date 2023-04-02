package com.agileavengers.icuconnectbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue
    private Long id;

    private String username;

    private String email;

    private String password;

    @ManyToMany()
    private List<Community> subscriptionList = new ArrayList<>();

}
