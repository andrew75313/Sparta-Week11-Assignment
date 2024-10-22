package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.entity.UserRoleEnum;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.like.LikeRepository;
import com.sparta.realestatefeed.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ApartRepositoryTest {

    @Autowired
    private ApartRepository apartRepository;

    @Autowired
    private UserRepository userRepository;

    private Apart apart;
    private User user;

    private Apart setTestApart(String apartName) {

        Apart apart = new Apart();
        ReflectionTestUtils.setField(apart, "apartName", apartName);

        return apart;
    }

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
        apart = new Apart();
        user = new User();
    }

    @Nested
    @DisplayName("특정 아파트 조회")
    class FindByApartIdTest {

        @Test
        @DisplayName("특정 아파트가 존재할 경우")
        void testFindByApartIdSuccess() {
            // given
            apart = setTestApart("testapart");
            apartRepository.save(apart);

            // when
            Optional<Apart> optionalApart = apartRepository.findByApartId(apart.getId());

            // then
            assertTrue(optionalApart.isPresent());
            assertEquals(apart.getId(), optionalApart.get().getId());
        }

        @Test
        @DisplayName("특정 아파트가 없을 경우")
        void testFindByApartIdFailure() {
            // given
            apart = setTestApart("testapart");
            apartRepository.save(apart);

            // when
            Optional<Apart> optionalApart = apartRepository.findByApartId(999L);

            // then
            assertFalse(optionalApart.isPresent());
        }
    }

    @Test
    @DisplayName("특정 아파트 게시글 작성자 조회")
    void testFindWriter() {
        // given
        user = setTestUser("testuser");
        userRepository.save(user);

        apart = setTestApart("testapart");
        ReflectionTestUtils.setField(apart, "user", user);
        apartRepository.save(apart);

        // when
        User writer = apartRepository.findWriter(apart.getId());

        // then
        assertEquals(user, writer);
    }

    @Test
    @DisplayName("지역별 아파트 게시글 조회")
    void testFindByAreaDateDescendingPaginated() {
        // given
        for (int index = 1; index <= 6; index++) {
            apart = setTestApart("seoulapart" + index);
            ReflectionTestUtils.setField(apart, "area", "Seoul");
            apartRepository.save(apart);
        }

        for (int index = 1; index <= 4; index++) {
            apart = setTestApart("incheonapart" + index);
            ReflectionTestUtils.setField(apart, "area", "Incheon");
            apartRepository.save(apart);
        }

        // when
        List<Apart> apartList = apartRepository.findByAreaDateDescendingPaginated("Seoul", 0);

        // then
        assertFalse(apartList.isEmpty());
        assertEquals(5, apartList.size());
        assertEquals(apartList.get(0).getArea(), "Seoul");
        assertEquals(apartList.get(1).getArea(), "Seoul");
        assertEquals(apartList.get(2).getArea(), "Seoul");
        assertEquals(apartList.get(3).getArea(), "Seoul");
        assertEquals(apartList.get(4).getArea(), "Seoul");
    }

    @Test
    @DisplayName("특정 사용자들이 작성한 아파트 게시글 모두 조회")
    void testFindByFollowingUsers() {
        // given
        List<Long> followingIdList = new ArrayList<>();

        User testUser1 = setTestUser("testUser1");
        userRepository.save(testUser1);

        Long testUser1Id = testUser1.getId();
        followingIdList.add(testUser1Id);

        for (int apartIndex = 1; apartIndex <= 3; apartIndex++) {
            apart = setTestApart("seoulapart" + apartIndex);
            ReflectionTestUtils.setField(apart, "area", "Seoul");
            ReflectionTestUtils.setField(apart, "user", testUser1);
            apartRepository.save(apart);
        }

        User testUser2 = setTestUser("testUser2");
        userRepository.save(testUser2);

        Long testUser2Id = testUser2.getId();
        followingIdList.add(testUser2Id);

        for (int apartIndex = 1; apartIndex <= 3; apartIndex++) {
            apart = setTestApart("seoulapart" + apartIndex);
            ReflectionTestUtils.setField(apart, "area", "Seoul");
            ReflectionTestUtils.setField(apart, "user", testUser2);
            apartRepository.save(apart);
        }

        // when
        List<Apart> apartList = apartRepository.findByFollowingUsers(followingIdList, 0);

        // then
        assertFalse(apartList.isEmpty());
        assertEquals(5, apartList.size());
        assertEquals(apartList.get(0).getUser().getId(), testUser2Id);
        assertEquals(apartList.get(1).getUser().getId(), testUser2Id);
        assertEquals(apartList.get(2).getUser().getId(), testUser2Id);
        assertEquals(apartList.get(3).getUser().getId(), testUser1Id);
        assertEquals(apartList.get(4).getUser().getId(), testUser1Id);
    }
}
