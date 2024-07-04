package com.sparta.realestatefeed.repository.follow;

import com.sparta.realestatefeed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowJpaRepository {
}
