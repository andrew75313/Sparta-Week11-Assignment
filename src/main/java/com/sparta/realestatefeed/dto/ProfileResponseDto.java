package com.sparta.realestatefeed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.realestatefeed.entity.User;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDto {
    String userName;
    String nickName;
    String email;
    String info;
    Long followersCount;
    Long followingCount;
    Long aptLikesCount;
    Long qnaLikesCount;

    public ProfileResponseDto(User user) {
        this.userName = user.getUserName();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.info = user.getInfo();
        this.followersCount = user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
    }

    public void updateCounts(Long aptLikesCount, Long qnaLikesCount) {
        this.aptLikesCount = aptLikesCount;
        this.qnaLikesCount = qnaLikesCount;
    }
}
