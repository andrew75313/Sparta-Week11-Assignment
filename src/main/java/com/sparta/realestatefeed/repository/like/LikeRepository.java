package com.sparta.realestatefeed.repository.like;

import com.sparta.realestatefeed.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeJpaRepository{
}
