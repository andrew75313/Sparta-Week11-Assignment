package com.sparta.realestatefeed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.realestatefeed.entity.QnA;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QnAResponseDto {

    private Long qnaId;
    private String content;
    private Long likesCount;

    public QnAResponseDto(QnA qna) {
        this.qnaId = qna.getQnaId();
        this.content = qna.getContent();
    }

    public void updateLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }
}
