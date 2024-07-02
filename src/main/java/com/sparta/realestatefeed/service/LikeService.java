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

        QLike Like = QLike.like;
        QUser User = QUser.user;
        QApart Apart = QApart.apart;

        String message = "좋아요를 추가 했습니다.";

        Apart foundApart = jpaQueryFactory.selectFrom(Apart)
                .where(Apart.id.eq(apartId))
                .fetchOne();

        if (foundApart == null) {
            throw new NoSuchElementException("해당 아파트는 존재하지 않습니다.");
        }

        User writer = jpaQueryFactory.selectFrom(Apart)
                .innerJoin(Apart.user, User).fetchJoin()
                .where(Apart.id.eq(apartId))
                .fetchOne().getUser();

        if (writer.getUserName().equals(user.getUserName())) {
            throw new UserAlreadyExistsException("본인의 글에 좋아요를 누를 수 없습니다.");
        }

        Like foundLike = jpaQueryFactory.selectFrom(Like)
                .where(Like.apart.id.eq(apartId))
                .fetchOne();

        if (foundLike == null) {
            likeRepository.save(new Like(user, foundApart, null));
        } else {
            message = "좋아요를 삭제했습니다.";

            likeRepository.delete(foundLike);
        }

        return new CommonDto<>(HttpStatus.OK.value(), message, null);
    }
}
