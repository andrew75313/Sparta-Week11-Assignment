package com.sparta.realestatefeed.repository.like;

import com.sparta.realestatefeed.entity.Like;
import com.sparta.realestatefeed.entity.User;

import java.util.List;
import java.util.Optional;

public interface LikeJpaRepository {

    Optional<Like> findByApartIdAndUser(Long apartId, User user);
    Optional<Like> findByQnaIdAndUser(Long qnaId, User user);
    Long countApartLikes(Long apartId);
    Long countQnaLikes(Long qnaId);
    List<Like> findApartLikesPaginated(int page, User user);
    List<Like> findQnaLikesPaginated(int page, User user);
    Long countApartLikesByUser(User user);
    Long countQnaLikesByUser(User user);
}
