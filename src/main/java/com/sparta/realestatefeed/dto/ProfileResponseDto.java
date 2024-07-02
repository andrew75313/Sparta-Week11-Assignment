package com.sparta.realestatefeed.dto;

import com.sparta.realestatefeed.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    String userName;
    String nickName;
    String email;
    String info;
    Long aptLikesCount;
    Long qnaLikesCount;

    public ProfileResponseDto(User user) {
        this.userName = user.getUserName();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.info = user.getInfo();
    }

    public void updateCounts(Long aptLikesCount, Long qnaLikesCount) {
        this.aptLikesCount = aptLikesCount;
        this.qnaLikesCount = qnaLikesCount;
    }
}
