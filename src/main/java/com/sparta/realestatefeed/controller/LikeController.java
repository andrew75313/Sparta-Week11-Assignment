package com.sparta.realestatefeed.controller;

import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.security.UserDetailsImpl;
import com.sparta.realestatefeed.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/aparts/{apartId}/like")
    public ResponseEntity<?> likeApart(@PathVariable("apartId") Long apartId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommonDto<?> response = likeService.likeApart(apartId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
