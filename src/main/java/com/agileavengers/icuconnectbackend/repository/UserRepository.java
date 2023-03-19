package com.agileavengers.icuconnectbackend.repository;

import com.agileavengers.icuconnectbackend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional()
    void deleteUserById(Long id);
}
