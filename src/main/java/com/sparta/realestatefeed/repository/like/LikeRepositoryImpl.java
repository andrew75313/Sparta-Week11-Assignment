package com.sparta.realestatefeed.repository.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.*;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeJpaRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QLike qLike = QLike.like;
    private final QApart qApart = QApart.apart;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<Like> findByApartIdAndUser(Long apartId, User user){

        Like like = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.apart.id.eq(apartId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        return Optional.ofNullable(like);
    }

    @Override
    public Optional<Like> findByQnaIdAndUser(Long qnaId, User user){

        Like like = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.qna.qnaId.eq(qnaId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        return Optional.ofNullable(like);
    }


}
