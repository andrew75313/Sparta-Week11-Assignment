package com.sparta.realestatefeed.repository.user;

import com.sparta.realestatefeed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserJpaRepository {

    Optional<User> findByUserName(String username);

    boolean existsByUserName(String username);
}
