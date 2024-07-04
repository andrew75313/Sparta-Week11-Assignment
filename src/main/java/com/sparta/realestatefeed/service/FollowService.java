package com.sparta.realestatefeed.service;

import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.entity.Follow;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.exception.UserNotFoundException;
import com.sparta.realestatefeed.repository.follow.FollowRepository;
import com.sparta.realestatefeed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public CommonDto<?> followUser(Long userId, User user) {

        String message = "팔로우를 했습니다.";

        User follower = userRepository.findByUserName(user.getUserName()).orElseThrow(
                () -> new UserNotFoundException("다시 로그인 해주세요.")
        );

        User userToFollow = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("팔로우할 사용자는 존재하지 않습니다.")
        );

        if (userToFollow.getUserName().equals(user.getUserName())) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        Optional<Follow> optionalFollow = followRepository.findByFollowerAndFollowing(user, userToFollow);

        if (optionalFollow.isPresent()) {
            Follow foundFollow = optionalFollow.get();
            message = "팔로우를 취소했습니다.";
            followRepository.delete(foundFollow);
        } else {
            followRepository.save(new Follow(follower, userToFollow));
        }

        return new CommonDto<>(HttpStatus.OK.value(), message, null);
    }
}
