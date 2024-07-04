package com.sparta.realestatefeed.repository.follow;

import com.sparta.realestatefeed.entity.Follow;
import com.sparta.realestatefeed.entity.User;

import java.util.List;
import java.util.Optional;

public interface FollowJpaRepository {

    Optional<Follow> findByFollowerAndFollowing(User user, User userToFollow);
    List<Follow> findFollowingUserByFollower(User follower);
}
