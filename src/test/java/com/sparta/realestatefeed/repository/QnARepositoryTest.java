package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.QnA;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.entity.UserRoleEnum;
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
public class QnARepositoryTest {

    @Autowired
    private QnARepository qnARepository;

    @Autowired
    private UserRepository userRepository;

    private QnA qna;
    private User user;

    private QnA setTestQna(String content) {

        QnA qna = new QnA();
        ReflectionTestUtils.setField(qna, "content", content);
        ReflectionTestUtils.setField(qna, "isCompleted", false);

        return qna;
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
        qna = new QnA();
        user = new User();
    }

    @Nested
    @DisplayName("특정 문의 댓글 조회")
    class FindByQnaIdTest {

        @Test
        @DisplayName("특정 문의 댓글이 존재할 경우")
        void testFindByQnaIdSuccess() {
            // given
            qna = setTestQna("testcontents");
            qnARepository.save(qna);

            // when
            Optional<QnA> optionalQnA = qnARepository.findByQnaId(qna.getQnaId());

            // then
            assertTrue(optionalQnA.isPresent());
            assertEquals(qna.getQnaId(), optionalQnA.get().getQnaId());
        }

        @Test
        @DisplayName("특정 문의 댓글이 없을 경우")
        void testFindByQnaIdFailure() {
            // given
            qna = setTestQna("testcontents");
            qnARepository.save(qna);

            // when
            Optional<QnA> optionalQnA = qnARepository.findByQnaId(999L);

            // then
            assertFalse(optionalQnA.isPresent());
        }
    }

    @Test
    @DisplayName("특정 문의 댓글 작성자 조회")
    void testFindWriter() {
        // given
        user = setTestUser("testuser");
        userRepository.save(user);

        qna = setTestQna("testcontents");
        ReflectionTestUtils.setField(qna, "user", user);
        qnARepository.save(qna);

        // when
        User writer = qnARepository.findWriter(qna.getQnaId());

        // then
        assertEquals(user, writer);
    }

}
