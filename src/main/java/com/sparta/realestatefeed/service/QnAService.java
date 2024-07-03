package com.sparta.realestatefeed.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.dto.QnARequestDto;
import com.sparta.realestatefeed.dto.QnAResponseDto;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.like.LikeRepository;
import com.sparta.realestatefeed.repository.qna.QnARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final QnARepository qnARepository;
    private final ApartRepository apartRepository;
    private final LikeRepository likeRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public CommonDto<QnAResponseDto> create(Long apartId, QnARequestDto qnARequestDto, User user) {

        Apart apart = apartRepository.findById(apartId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 아파트를 찾을 수 없습니다."));

        if (apart.getIsSaled() == ApartStatusEnum.SOLD) {
            throw new NoSuchElementException("매매중인 아파트가 존재하지 않습니다.");
        }

        QnA saveQnA = new QnA(qnARequestDto.getContent(), user, apart);

        qnARepository.save(saveQnA);

        QnAResponseDto responseDto = new QnAResponseDto(saveQnA);

        CommonDto<QnAResponseDto> commonDto = new CommonDto<QnAResponseDto>(HttpStatus.OK.value(), "문의 등록에 성공하셨습니다.", responseDto);

        return commonDto;

    }

    public CommonDto<QnAResponseDto> select(Long qnaId) {

        QLike qLike = QLike.like;

        QnA qna = qnARepository.findById(qnaId)
                .orElseThrow(() -> new NoSuchElementException("요청하신 댓글이 존재하지 않습니다."));

        QnAResponseDto responseDto = new QnAResponseDto(qna);

        Long likesCount = likeRepository.countQnaLikes(qnaId);

        responseDto.updateLikesCount(likesCount);

        CommonDto<QnAResponseDto> commonDto = new CommonDto<QnAResponseDto>(HttpStatus.OK.value(), "문의 조회에 성공하셨습니다.", responseDto);

        return commonDto;

    }

    @Transactional
    public CommonDto<QnAResponseDto> updateQnA(Long qnaId, QnARequestDto qnARequestDto, User user) {

        QnA qna = qnARepository.findById(qnaId)
                .orElseThrow(() -> new NoSuchElementException("요청하신 댓글이 존재하지 않습니다."));

        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (!user.getId().equals(qna.getUser().getId())) {
                throw new AccessDeniedException("직접 작성한 댓글만 수정할 수 있습니다.");
            }
        }

        qna.changeContent(qnARequestDto.getContent());

        QnAResponseDto responseDto = new QnAResponseDto(qna);

        CommonDto<QnAResponseDto> commonDto = new CommonDto<QnAResponseDto>(HttpStatus.OK.value(), "문의 수정에 성공하셨습니다.", responseDto);

        return commonDto;

    }

    public void deleteQnA(Long qnaId, User user) {

        QnA qna = qnARepository.findById(qnaId)
                .orElseThrow(() -> new NoSuchElementException("요청하신 댓글이 존재하지 않습니다."));

        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (!user.getId().equals(qna.getUser().getId())) {
                throw new AccessDeniedException("직접 작성한 댓글만 삭제할 수 있습니다.");
            }
        }

        qnARepository.deleteById(qna.getQnaId());

    }

    public CommonDto<List<QnAResponseDto>> selectByApartId(Long apartId, int page) {

        QQnA qQnA = QQnA.qnA;

        List<QnA> qnaList = jpaQueryFactory.selectFrom(qQnA)
                .where(qQnA.apart.id.eq(apartId))
                .orderBy(qQnA.createdAt.desc())
                .offset(page * 5)
                .limit(5)
                .fetch();

        List<QnAResponseDto> qnAResponseDtoList = qnaList.stream()
                .map(QnAResponseDto::new)
                .toList();

        CommonDto<List<QnAResponseDto>> commonDto = new CommonDto<>(HttpStatus.OK.value(), "아파트에 대한 문의 조회에 성공하셨습니다.", qnAResponseDtoList);

        return commonDto;
    }
}
