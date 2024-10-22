package com.sparta.realestatefeed.repository.like;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.util.SizingConstants;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QLike qLike = QLike.like;
    private final QApart qApart = QApart.apart;
    private final QQnA qQna = QQnA.qnA;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<Like> findByApartIdAndUser(Long apartId, User user) {

        Like like = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.apart.id.eq(apartId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        return Optional.ofNullable(like);
    }

    @Override
    public Optional<Like> findByQnaIdAndUser(Long qnaId, User user) {

        Like like = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.qna.qnaId.eq(qnaId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        return Optional.ofNullable(like);
    }

    @Override
    public Long countApartLikes(Long apartId) {

        Long likesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.apart.id.eq(apartId))
                .fetchOne();

        return likesCount;
    }

    @Override
    public Long countQnaLikes(Long qnaId) {

        Long likesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.qna.qnaId.eq(qnaId))
                .fetchOne();

        return likesCount;
    }

    @Override
    public List<Like> findApartLikesPaginated(int page, User user) {

        List<Like> likeList = jpaQueryFactory.selectFrom(qLike)
                .innerJoin(qLike.apart, qApart).fetchJoin()
                .innerJoin(qLike.user, qUser).fetchJoin()
                .where(qUser.userName.eq(user.getUserName()))
                .orderBy(qLike.apart.createdAt.desc())
                .offset(page * SizingConstants.PAGE_SIZE)
                .limit(SizingConstants.PAGE_SIZE)
                .fetch();

        return likeList;
    }

    @Override
    public List<Like> findQnaLikesPaginated(int page, User user) {

        List<Like> likeList = jpaQueryFactory.selectFrom(qLike)
                .innerJoin(qLike.qna, qQna).fetchJoin()
                .innerJoin(qLike.user, qUser).fetchJoin()
                .where(qUser.userName.eq(user.getUserName()))
                .orderBy(qLike.qna.createdAt.desc())
                .offset(page * SizingConstants.PAGE_SIZE)
                .limit(SizingConstants.PAGE_SIZE)
                .fetch();

        return likeList;
    }

    @Override
    public Long countApartLikesByUser(User user) {

        Long likesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.user.id.eq(user.getId())
                        .and(qLike.apart.isNotNull()))
                .fetchOne();

        return likesCount;
    }

    @Override
    public Long countQnaLikesByUser(User user) {

        Long likesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.user.id.eq(user.getId())
                        .and(qLike.qna.isNotNull()))
                .fetchOne();

        return likesCount;
    }
}
