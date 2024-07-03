package com.sparta.realestatefeed.service;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.dto.ApartResponseDto;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.dto.QnAResponseDto;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.exception.UserAlreadyExistsException;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.like.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ApartRepository apartRepository;
    private final LikeRepository likeRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public CommonDto<String> likeApart(Long apartId, User user) {

        String message = "좋아요를 추가 했습니다.";

        Apart foundApart = apartRepository.findByApartId(apartId).orElseThrow(
                () -> new NoSuchElementException("해당 아파트는 존재하지 않습니다.")
        );

        User writer = apartRepository.findWriter(apartId);

        if (writer.getUserName().equals(user.getUserName())) {
            throw new UserAlreadyExistsException("본인의 글에 좋아요를 누를 수 없습니다.");
        }

        Optional<Like> optionalLike = likeRepository.findByApartIdAndUser(apartId, user);

        if (optionalLike.isPresent()) {
            Like foundLike = optionalLike.get();
            message = "좋아요를 삭제했습니다.";
            likeRepository.delete(foundLike);
        } else {
            likeRepository.save(new Like(user, foundApart, null));
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

    public CommonDto<?> getFavroiteAparts(int page, User user) {

        QLike qLike = QLike.like;
        QUser qUser = QUser.user;
        QApart qApart = QApart.apart;

        List<Like> likeList = jpaQueryFactory.selectFrom(qLike)
                .innerJoin(qLike.apart, qApart).fetchJoin()
                .innerJoin(qLike.user, qUser).fetchJoin()
                .where(qUser.userName.eq(user.getUserName()))
                .orderBy(qLike.apart.createdAt.desc())
                .offset(page * 5)
                .limit(5)
                .fetch();

        if (likeList.isEmpty()) {
            throw new NoSuchElementException("좋아요한 아파트 판매글이 없습니다.");
        }

        List<ApartResponseDto> responseDtoList = likeList.stream()
                .map(Like -> new ApartResponseDto(Like.getApart()))
                .toList();

        return new CommonDto<>(HttpStatus.OK.value(), "좋아요한 아파트 조회에 성공하였습니다.", responseDtoList);
    }

    public CommonDto<?> getFavoriteQnas(int page, User user) {

        QLike qLike = QLike.like;
        QUser qUser = QUser.user;
        QQnA qQna = QQnA.qnA;

        List<Like> likeList = jpaQueryFactory.selectFrom(qLike)
                .innerJoin(qLike.qna, qQna).fetchJoin()
                .innerJoin(qLike.user, qUser).fetchJoin()
                .where(qUser.userName.eq(user.getUserName()))
                .orderBy(qLike.qna.createdAt.desc())
                .offset(page * 5)
                .limit(5)
                .fetch();

        if (likeList.isEmpty()) {
            throw new NoSuchElementException("좋아요한 문의글이 없습니다.");
        }

        List<QnAResponseDto> responseDtoList = likeList.stream()
                .map(Like -> new QnAResponseDto(Like.getQna()))
                .toList();

        for (QnAResponseDto qnAResponseDto : responseDtoList) {

            Long likesCount = jpaQueryFactory.select(Wildcard.count)
                    .from(qLike)
                    .where(qLike.qna.qnaId.eq(qnAResponseDto.getQnaId()))
                    .fetchOne();

            qnAResponseDto.updateLikesCount(likesCount);
        }

        return new CommonDto<>(HttpStatus.OK.value(), "좋아요한 문의글 조회에 성공하였습니다.", responseDtoList);
    }
}
