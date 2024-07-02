package com.sparta.realestatefeed.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.exception.UserAlreadyExistsException;
import com.sparta.realestatefeed.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public CommonDto<String> likeApart(Long apartId, User user) {

        QLike qLike = QLike.like;
        QUser qUser = QUser.user;
        QApart qApart = QApart.apart;

        String message = "좋아요를 추가 했습니다.";

        Apart foundApart = jpaQueryFactory.selectFrom(qApart)
                .where(qApart.id.eq(apartId))
                .fetchOne();

        if (foundApart == null) {
            throw new NoSuchElementException("해당 아파트는 존재하지 않습니다.");
        }

        User writer = jpaQueryFactory.selectFrom(qApart)
                .innerJoin(qApart.user, qUser).fetchJoin()
                .where(qApart.id.eq(apartId))
                .fetchOne().getUser();

        if (writer.getUserName().equals(user.getUserName())) {
            throw new UserAlreadyExistsException("본인의 글에 좋아요를 누를 수 없습니다.");
        }

        Like foundLike = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.apart.id.eq(apartId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        if (foundLike == null) {
            likeRepository.save(new Like(user, foundApart, null));
        } else {
            message = "좋아요를 삭제했습니다.";

            likeRepository.delete(foundLike);
        }

        return new CommonDto<>(HttpStatus.OK.value(), message, null);
    }

    public CommonDto<?> likeQna(Long qnaId, User user) {

        QLike qLike = QLike.like;
        QUser qUser = QUser.user;
        QQnA qQna = QQnA.qnA;

        String message = "좋아요를 추가 했습니다.";

        QnA foundQna = jpaQueryFactory.selectFrom(qQna)
                .where(qQna.qnaId.eq(qnaId))
                .fetchOne();

        if (foundQna == null) {
            throw new NoSuchElementException("해당 문의는 존재하지 않습니다.");
        }

        User writer = jpaQueryFactory.selectFrom(qQna)
                .innerJoin(qQna.user, qUser).fetchJoin()
                .where(qQna.qnaId.eq(qnaId))
                .fetchOne().getUser();

        if (writer.getUserName().equals(user.getUserName())) {
            throw new UserAlreadyExistsException("본인의 문의글에 좋아요를 누를 수 없습니다.");
        }

        Like foundLike = jpaQueryFactory.selectFrom(qLike)
                .where(qLike.qna.qnaId.eq(qnaId).and(qLike.user.userName.eq(user.getUserName())))
                .fetchOne();

        if (foundLike == null) {
            likeRepository.save(new Like(user, null, foundQna));
        } else {
            message = "좋아요를 삭제했습니다.";

            likeRepository.delete(foundLike);
        }

        return new CommonDto<>(HttpStatus.OK.value(), message, null);
    }
}
