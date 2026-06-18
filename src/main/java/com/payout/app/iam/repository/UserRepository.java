package com.payout.app.iam.repository;


import com.payout.app.iam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);

    Boolean existsByEmail(String email);
    Boolean existsByUserId(Long userId);
}
