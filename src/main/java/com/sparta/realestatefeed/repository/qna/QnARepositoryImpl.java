package com.sparta.realestatefeed.repository.qna;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.QQnA;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.QnA;
import com.sparta.realestatefeed.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class QnARepositoryImpl implements QnAJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QQnA qQna = QQnA.qnA;
    private final QUser qUser = QUser.user;

    @Override
    public Optional<QnA> findByQnaId(Long qnaId) {

        QnA qna = jpaQueryFactory.selectFrom(qQna)
                .where(qQna.qnaId.eq(qnaId))
                .fetchOne();

        return Optional.ofNullable(qna);
    }

    @Override
    public User findWriter(Long qnaId) {

        User user = jpaQueryFactory.selectFrom(qQna)
                .innerJoin(qQna.user, qUser).fetchJoin()
                .where(qQna.qnaId.eq(qnaId))
                .fetchOne().getUser();

        return user;
    }
}
