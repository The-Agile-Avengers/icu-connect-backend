package com.agileavengers.icuconnectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agileavengers.icuconnectbackend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);
}
