package com.sparta.realestatefeed.repository.like;

import com.sparta.realestatefeed.entity.Like;
import com.sparta.realestatefeed.entity.User;

import java.util.Optional;

public interface LikeJpaRepository {

    Optional<Like> findByApartIdAndUser(Long apartId, User user);
    Optional<Like> findByQnaIdAndUser(Long qnaId, User user);
    Long countApartLikes(Long apartId);
}
