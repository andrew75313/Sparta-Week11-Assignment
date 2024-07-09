package com.sparta.realestatefeed.repository.qna;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.entity.QQnA;
import com.sparta.realestatefeed.entity.QUser;
import com.sparta.realestatefeed.entity.QnA;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.util.SizingConstants;
import lombok.RequiredArgsConstructor;

import java.util.List;
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

    @Override
    public List<QnA> findByApartIdDescendingPaginated(Long apartId, int page) {

        List<QnA> qnaList = jpaQueryFactory.selectFrom(qQna)
                .where(qQna.apart.id.eq(apartId))
                .orderBy(qQna.createdAt.desc())
                .offset(page * SizingConstants.PAGE_SIZE)
                .limit(SizingConstants.PAGE_SIZE)
                .fetch();

        return qnaList;
    }
}
