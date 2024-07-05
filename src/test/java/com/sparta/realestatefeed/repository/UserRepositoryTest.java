package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.entity.UserRoleEnum;
import com.sparta.realestatefeed.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User setTestUser(String userName) {

        User user = new User();
        user.setUserName(userName);
        user.setPassword("password");
        user.setEmail(userName + "@email.com");
        user.setRole(UserRoleEnum.USER);

        return user;
    }

    @Test
    @DisplayName("팔로워 상위 10명 사용자 조회")
    void testGetTopFollowerUser() {
        // given
        for (int index = 1; index <= 15; index++) {
            User user = setTestUser("testuser" + index);

            Long followersCount = Long.valueOf(index);
            user.updateFollowCounts(followersCount, null);

            userRepository.save(user);
        }

        // when
        List<User> topUserList = userRepository.getTopFollowerUser();

        // then
        assertFalse(topUserList.isEmpty());
        assertEquals(10, topUserList.size());
        assertEquals(15L, topUserList.get(0).getFollowersCount());
        assertEquals(14L, topUserList.get(1).getFollowersCount());
        assertEquals(13L, topUserList.get(2).getFollowersCount());
        assertEquals(12L, topUserList.get(3).getFollowersCount());
        assertEquals(11L, topUserList.get(4).getFollowersCount());
        assertEquals(10L, topUserList.get(5).getFollowersCount());
        assertEquals(9L, topUserList.get(6).getFollowersCount());
        assertEquals(8L, topUserList.get(7).getFollowersCount());
        assertEquals(7L, topUserList.get(8).getFollowersCount());
        assertEquals(6L, topUserList.get(9).getFollowersCount());
    }
}
