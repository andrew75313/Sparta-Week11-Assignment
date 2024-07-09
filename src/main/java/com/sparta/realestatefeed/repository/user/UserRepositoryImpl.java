package com.sparta.realestatefeed.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.QLike;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.util.SizingConstants;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser qUser = QUser.user;
    private final QLike qLike = QLike.like;

    @Override
    public List<User> getTopFollowerUser() {

        List<User> topUserList = jpaQueryFactory.selectFrom(qUser)
                .orderBy(qUser.followersCount.desc())
                .limit(SizingConstants.RANK_SIZE)
                .fetch();

        return topUserList;
    }
}
