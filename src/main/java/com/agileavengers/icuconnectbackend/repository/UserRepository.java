package com.agileavengers.icuconnectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agileavengers.icuconnectbackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
