package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.QnA;
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

import java.util.List;
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

    @Autowired
    private ApartRepository apartRepository;

    private QnA qna;
    private Apart apart;
    private User user;

    private QnA setTestQna(String content) {

        QnA qna = new QnA();
        ReflectionTestUtils.setField(qna, "content", content);
        ReflectionTestUtils.setField(qna, "isCompleted", false);

        return qna;
    }

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
        qna = new QnA();
        apart = new Apart();
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

    @Test
    @DisplayName("특정 아파트의 전체 문의 댓글 조회")
    void testFindByApartIdDescendingPaginated() {
        // given
        apart = setTestApart("testapart");
        apartRepository.save(apart);

        for (int index = 1; index <= 6; index++) {
            qna = setTestQna("testcontents" + index);
            ReflectionTestUtils.setField(qna, "apart", apart);
            qnARepository.save(qna);
        }

        // when
        List<QnA> qnAList = qnARepository.findByApartIdDescendingPaginated(apart.getId(), 0);

        // then
        assertFalse(qnAList.isEmpty());
        assertEquals(5, qnAList.size());
        assertEquals(apart.getId(), qnAList.get(0).getApart().getId());
        assertEquals(apart.getId(), qnAList.get(1).getApart().getId());
        assertEquals(apart.getId(), qnAList.get(2).getApart().getId());
        assertEquals(apart.getId(), qnAList.get(3).getApart().getId());
        assertEquals(apart.getId(), qnAList.get(4).getApart().getId());
    }
}
