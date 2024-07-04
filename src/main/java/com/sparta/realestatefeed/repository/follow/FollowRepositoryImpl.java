package com.sparta.realestatefeed.repository.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.Follow;
import com.sparta.realestatefeed.entity.QFollow;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.User;
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
    public List<Follow> findFollowingUserByFollower(User follower) {

        List<Follow> followList = jpaQueryFactory.selectFrom(qFollow)
                .where(qFollow.follower.id.eq(follower.getId()))
                .fetch();

        return followList;
    }
}
