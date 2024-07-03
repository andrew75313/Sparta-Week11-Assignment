package com.sparta.realestatefeed.service;

import com.sparta.realestatefeed.dto.ApartResponseDto;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.dto.QnAResponseDto;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.entity.Like;
import com.sparta.realestatefeed.entity.QnA;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.exception.UserAlreadyExistsException;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.like.LikeRepository;
import com.sparta.realestatefeed.repository.qna.QnARepository;
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
    private final QnARepository qnARepository;
    private final LikeRepository likeRepository;

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

        String message = "좋아요를 추가 했습니다.";

        QnA foundQna = qnARepository.findByQnaId(qnaId).orElseThrow(
                () -> new NoSuchElementException("해당 문의는 존재하지 않습니다.")
        );

        User writer = qnARepository.findWriter(qnaId);

        if (writer.getUserName().equals(user.getUserName())) {
            throw new UserAlreadyExistsException("본인의 문의글에 좋아요를 누를 수 없습니다.");
        }

        Optional<Like> optionalLike = likeRepository.findByQnaIdAndUser(qnaId, user);

        if (optionalLike.isPresent()) {
            Like foundLike = optionalLike.get();
            message = "좋아요를 삭제했습니다.";
            likeRepository.delete(foundLike);
        } else {
            likeRepository.save(new Like(user, null, foundQna));
        }

        return new CommonDto<>(HttpStatus.OK.value(), message, null);
    }

    public CommonDto<?> getFavroiteAparts(int page, User user) {

        List<Like> likeList = likeRepository.findApartLikesPaginated(page, user);

        if (likeList.isEmpty()) {
            throw new NoSuchElementException("좋아요한 아파트 판매글이 없습니다.");
        }

        List<ApartResponseDto> responseDtoList = likeList.stream()
                .map(Like -> new ApartResponseDto(Like.getApart()))
                .toList();

        return new CommonDto<>(HttpStatus.OK.value(), "좋아요한 아파트 조회에 성공하였습니다.", responseDtoList);
    }

    public CommonDto<?> getFavoriteQnas(int page, User user) {

        List<Like> likeList = likeRepository.findQnaLikesPaginated(page, user);

        if (likeList.isEmpty()) {
            throw new NoSuchElementException("좋아요한 문의글이 없습니다.");
        }

        List<QnAResponseDto> responseDtoList = likeList.stream()
                .map(Like -> new QnAResponseDto(Like.getQna()))
                .toList();

        for (QnAResponseDto qnAResponseDto : responseDtoList) {

            Long likesCount = likeRepository.countQnaLikes(qnAResponseDto.getQnaId());

            qnAResponseDto.updateLikesCount(likesCount);
        }

        return new CommonDto<>(HttpStatus.OK.value(), "좋아요한 문의글 조회에 성공하였습니다.", responseDtoList);
    }
}
