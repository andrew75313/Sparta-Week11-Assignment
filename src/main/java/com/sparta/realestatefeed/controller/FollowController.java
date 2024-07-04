package com.sparta.realestatefeed.controller;

import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.security.UserDetailsImpl;
import com.sparta.realestatefeed.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{userId}")
    public ResponseEntity<?> followUser(@PathVariable("userId") Long userId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommonDto<?> response = followService.followUser(userId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/followers/aparts")
    public ResponseEntity<?> getFollowersAparts(@RequestParam(value = "username", required = false) String followingUserName,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommonDto<?> response = followService.getFollowersAparts(followingUserName, page - 1, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
