package com.sparta.realestatefeed.controller;

import com.sparta.realestatefeed.dto.ApartRequestDto;
import com.sparta.realestatefeed.dto.ApartResponseDto;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.security.UserDetailsImpl;
import com.sparta.realestatefeed.service.ApartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aparts")
public class ApartController {

    private final ApartService apartService;

    public ApartController(ApartService apartService) {
        this.apartService = apartService;
    }


    @PostMapping
    public ResponseEntity<CommonDto<ApartResponseDto>> createApart(@RequestBody ApartRequestDto apartRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommonDto<ApartResponseDto> responseDto = apartService.createApart(apartRequestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonDto<ApartResponseDto>> getApart(@PathVariable Long id) {
        CommonDto<ApartResponseDto> responseDto = apartService.getApart(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<CommonDto<List<ApartResponseDto>>> getAparts(@RequestParam(required = false) String area,
                                                                       @RequestParam(defaultValue = "1") int page) {

        CommonDto<List<ApartResponseDto>> responseDtos = apartService.getAparts(area, page-1);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonDto<ApartResponseDto>> updateApart(@PathVariable Long id, @RequestBody ApartRequestDto apartRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommonDto<ApartResponseDto> responseDto = apartService.updateApart(id, apartRequestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDto<String>> deleteApart(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommonDto<String> responseDto = apartService.deleteApart(id, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
