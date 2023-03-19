package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
