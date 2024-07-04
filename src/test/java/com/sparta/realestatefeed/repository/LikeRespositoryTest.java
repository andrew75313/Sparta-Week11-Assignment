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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
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
        user.setEmail(userName + "@email.com");
        user.setRole(UserRoleEnum.USER);

        return user;
    }

    private Apart setTestApart(String apartName) {

        Apart apart = new Apart();
        ReflectionTestUtils.setField(apart, "apartName", apartName);

        return apart;
    }

    private QnA setTestQna(String content) {

        QnA qna = new QnA();
        ReflectionTestUtils.setField(qna, "content", content);
        ReflectionTestUtils.setField(qna, "isCompleted", false);

        return qna;
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
            Optional<Like> optionalLike = likeRepository.findByApartIdAndUser(999L, user);

            // then
            assertFalse(optionalLike.isPresent());
        }
    }

    @Nested
    @DisplayName("특정 문의 댓글에 사용자가 추가한 좋아요 찾기")
    class FindByQnaIdAndUserTest {

        @Test
        @DisplayName("좋아요가 있을 경우")
        void testFindByQnaIdAndUserSucess() {
            // give
            user = setTestUser("testuser");
            userRepository.save(user);

            qna = setTestQna("test contents");
            qnARepository.save(qna);

            like = new Like(user, null, qna);
            likeRepository.save(like);

            // when
            Optional<Like> optionalLike = likeRepository.findByQnaIdAndUser(qna.getQnaId(), user);

            // then
            assertTrue(optionalLike.isPresent());
            assertEquals("testuser", optionalLike.get().getUser().getUserName());
            assertEquals(qna.getQnaId(), optionalLike.get().getQna().getQnaId());
        }

        @Test
        @DisplayName("좋아요가 없을 경우")
        void testFindByQnaIdAndUserFailure() {
            // give
            user = setTestUser("testuser");
            userRepository.save(user);

            // when
            Optional<Like> optionalLike = likeRepository.findByQnaIdAndUser(999L, user);

            // then
            assertFalse(optionalLike.isPresent());
        }
    }

    @Test
    @DisplayName("특정 아파트 게시글의 좋아요 갯수 조회")
    void testCountApartLikes() {
        // give
        User testUser1 = setTestUser("testuser1");
        userRepository.save(testUser1);

        User testUser2 = setTestUser("testuser2");
        userRepository.save(testUser2);

        User testUser3 = setTestUser("testuser3");
        userRepository.save(testUser3);

        apartRepository.save(apart);

        likeRepository.save(new Like(testUser1, apart, null));
        likeRepository.save(new Like(testUser2, apart, null));
        likeRepository.save(new Like(testUser3, apart, null));

        // when
        Long countLikes = likeRepository.countApartLikes(apart.getId());

        // then
        assertEquals(3L, countLikes);
    }

    @Test
    @DisplayName("특정 문의 댓글의 좋아요 갯수 조회")
    void testCountQnaLikes() {
        // give
        User testUser1 = setTestUser("testuser1");
        userRepository.save(testUser1);

        User testUser2 = setTestUser("testuser2");
        userRepository.save(testUser2);

        User testUser3 = setTestUser("testuser3");
        userRepository.save(testUser3);

        qna = setTestQna("test contents");
        qnARepository.save(qna);

        likeRepository.save(new Like(testUser1, null, qna));
        likeRepository.save(new Like(testUser2, null, qna));
        likeRepository.save(new Like(testUser3, null, qna));

        // when
        Long countLikes = likeRepository.countQnaLikes(qna.getQnaId());

        // then
        assertEquals(3L, countLikes);
    }

    @Test
    @DisplayName("사용자의 아파드 게시글에 대해 추가한 모든 좋아요 조회")
    void testFindApartLikesPaginated() {
        // give
        user = setTestUser("testuser");
        userRepository.save(user);

        for (int index = 1; index <= 6; index++) {
            apart = setTestApart("testapart" + index);
            apartRepository.save(apart);
            likeRepository.save(new Like(user, apart, null));
        }

        // when
        List<Like> likeList = likeRepository.findApartLikesPaginated(0, user);

        // then
        assertFalse(likeList.isEmpty());
        assertEquals(5, likeList.size());
        assertEquals(user.getId(), likeList.get(0).getUser().getId());
        assertEquals(user.getId(), likeList.get(1).getUser().getId());
        assertEquals(user.getId(), likeList.get(2).getUser().getId());
        assertEquals(user.getId(), likeList.get(3).getUser().getId());
        assertEquals(user.getId(), likeList.get(4).getUser().getId());
    }

    @Test
    @DisplayName("사용자의 댓글 문의에 대해 추가한 모든 좋아요 조회")
    void testFindQnaLikesPaginated() {
        // give
        user = setTestUser("testuser");
        userRepository.save(user);

        for (int index = 1; index <= 6; index++) {
            qna = setTestQna("testcontent" + index);
            qnARepository.save(qna);
            likeRepository.save(new Like(user, null, qna));
        }

        // when
        List<Like> likeList = likeRepository.findQnaLikesPaginated(0, user);

        // then
        assertFalse(likeList.isEmpty());
        assertEquals(5, likeList.size());
        assertEquals(user.getId(), likeList.get(0).getUser().getId());
        assertEquals(user.getId(), likeList.get(1).getUser().getId());
        assertEquals(user.getId(), likeList.get(2).getUser().getId());
        assertEquals(user.getId(), likeList.get(3).getUser().getId());
        assertEquals(user.getId(), likeList.get(4).getUser().getId());
    }
}
