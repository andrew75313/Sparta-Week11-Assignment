package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.like.LikeRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class LikeRespositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartRepository apartRepository;

    @Autowired
    private QnARepository qnARepository;

    private Like like;
    private User user;
    private Apart apart;
    private QnA qna;

    private User setTestUser(String userName) {

        User user = new User();
        user.setUserName(userName);
        user.setPassword("password");
        user.setEmail("test@email.com");
        user.setRole(UserRoleEnum.USER);

        return user;
    }

    @BeforeEach
    void setUp() {

        like = new Like();
        user = new User();
        apart = new Apart();
        qna = new QnA();
    }

    @Nested
    @DisplayName("특정 아파트 게시글에 사용자가 추가한 좋아요 찾기")
    class FindByApartIdAndUserTest {

        @Test
        @DisplayName("좋아요가 있을 경우")
        void testFindByApartIdAndUserSucess() {
            // give
            user = setTestUser("testuser");
            userRepository.save(user);

            apartRepository.save(apart);

            like = new Like(user, apart, null);
            likeRepository.save(like);

            // when
            Optional<Like> optionalLike = likeRepository.findByApartIdAndUser(apart.getId(), user);

            // then
            assertTrue(optionalLike.isPresent());
            assertEquals("testuser", optionalLike.get().getUser().getUserName());
            assertEquals(apart.getId(), optionalLike.get().getApart().getId());
        }

        @Test
        @DisplayName("좋아요가 없을 경우")
        void testFindByApartIdAndUserFailure() {
            // give
            user = setTestUser("testuser");
            userRepository.save(user);

            // when
            Optional<Like> optionalLike = likeRepository.findByApartIdAndUser(1L, user);

            // then
            assertFalse(optionalLike.isPresent());
        }
    }



}
