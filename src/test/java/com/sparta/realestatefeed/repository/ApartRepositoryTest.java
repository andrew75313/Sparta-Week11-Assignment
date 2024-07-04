package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.entity.UserRoleEnum;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.qna.QnARepository;
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
}
