package com.sparta.realestatefeed.repository.follow;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QFollow qFollow = QFollow.follow;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<Follow> findByFollowerAndFollowing(User user, User userToFollow) {

        Follow follow = jpaQueryFactory.selectFrom(qFollow)
                .where(qFollow.follower.userName.eq(user.getUserName())
                        .and(qFollow.following.userName.eq(userToFollow.getUserName())))
                .fetchOne();

        return Optional.ofNullable(follow);
    }

    @Override
    public List<Follow> findByFollowerAndFollowingUserName(User follower, String followingUserName) {

        BooleanExpression query = qFollow.follower.id.eq(follower.getId());

        if (followingUserName != null) {
            query = query.and(qFollow.following.userName.eq(followingUserName));
        }

        List<Follow> followList = jpaQueryFactory.selectFrom(qFollow)
                .where(query)
                .fetch();

        return followList;
    }
}
