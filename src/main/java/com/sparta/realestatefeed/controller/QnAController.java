package com.sparta.realestatefeed.controller;

import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.dto.QnARequestDto;
import com.sparta.realestatefeed.dto.QnAResponseDto;
import com.sparta.realestatefeed.security.UserDetailsImpl;
import com.sparta.realestatefeed.service.QnAService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class QnAController {

    private QnAService qnAService;

    public QnAController(QnAService qnAService) {
        this.qnAService = qnAService;
    }

    @PostMapping("/aparts/{apartId}/qna")
    public ResponseEntity<CommonDto<QnAResponseDto>> createQnA(@PathVariable Long apartId, @RequestBody QnARequestDto qnARequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommonDto<QnAResponseDto> responseDto = qnAService.create(apartId, qnARequestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @GetMapping("aparts/{apartId}/qna/{qnaId}")
    public ResponseEntity<CommonDto<QnAResponseDto>> getQnA(@PathVariable Long qnaId) {

        CommonDto<QnAResponseDto> responseDto = qnAService.select(qnaId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @GetMapping("aparts/{apartId}/qna")
    public ResponseEntity<CommonDto<List<QnAResponseDto>>> getAllQnAForApartId(@PathVariable Long apartId,
                                                                               @RequestParam(defaultValue = "1") int page) {

        CommonDto<List<QnAResponseDto>> responseDto = qnAService.selectByApartId(apartId, page-1);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @PutMapping("aparts/{apartId}/qna/{qnaId}")
    public ResponseEntity<CommonDto<QnAResponseDto>> updateQnA(@PathVariable Long qnaId, @RequestBody QnARequestDto qnARequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommonDto<QnAResponseDto> responseDto = qnAService.updateQnA(qnaId, qnARequestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @DeleteMapping("aparts/{apartId}/qna/{qnaId}")
    public ResponseEntity<?> deleteQnA(@PathVariable Long qnaId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        qnAService.deleteQnA(qnaId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("문의 삭제에 성공하셨습니다.");

    }
}