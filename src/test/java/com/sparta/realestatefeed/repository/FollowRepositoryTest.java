package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.Follow;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.entity.UserRoleEnum;
import com.sparta.realestatefeed.repository.follow.FollowRepository;
import com.sparta.realestatefeed.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private Follow follow;

    private User setTestUser(String userName) {

        User user = new User();
        user.setUserName(userName);
        user.setPassword("password");
        user.setEmail(userName + "@email.com");
        user.setRole(UserRoleEnum.USER);

        return user;
    }

    @BeforeEach
    void setUp() {
        follow = new Follow();
    }

    @Nested
    @DisplayName("팔로우 한 사람과 팔로잉된 사람 간 팔로우 조회")
    class FndByFollowerAndFollowingTest {

        @Test
        @DisplayName("팔로우 한 사람이 팔로인된 사람을 팔로우 했을 경우")
        void testFindByFollowerAndFollowingSuccess() {
            // given
            User follower = setTestUser("follower");
            userRepository.save(follower);

            User following = setTestUser("following");
            userRepository.save(following);

            follow = new Follow(follower, following);
            followRepository.save(follow);

            // when
            Optional<Follow> optionalFollow = followRepository.findByFollowerAndFollowing(follower, following);

            // then
            assertTrue(optionalFollow.isPresent());
            assertEquals(follower.getId(), optionalFollow.get().getFollower().getId());
            assertEquals(following.getId(), optionalFollow.get().getFollowing().getId());
        }

        @Test
        @DisplayName("팔로우 한 사람이 팔로인된 사람을 팔로우 하지 않았을 경우")
        void testFindByFollowerAndFollowingFailure() {
            // given
            User follower = setTestUser("follower");
            userRepository.save(follower);

            User following1 = setTestUser("following1");
            userRepository.save(following1);

            User following2 = setTestUser("following2");
            userRepository.save(following2);

            follow = new Follow(follower, following1);
            followRepository.save(follow);

            // when
            Optional<Follow> optionalFollow = followRepository.findByFollowerAndFollowing(follower, following2);

            // then
            assertFalse(optionalFollow.isPresent());
        }
    }

}
