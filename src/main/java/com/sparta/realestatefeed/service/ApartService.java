package com.sparta.realestatefeed.service;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.dto.ApartRequestDto;
import com.sparta.realestatefeed.dto.ApartResponseDto;
import com.sparta.realestatefeed.dto.CommonDto;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.repository.ApartRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class ApartService {

    private final ApartRepository apartRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public ApartService(ApartRepository apartRepository, JPAQueryFactory jpaQueryFactory) {
        this.apartRepository = apartRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Transactional
    public CommonDto<ApartResponseDto> createApart(ApartRequestDto requestDto, User user) {

        Apart apart = new Apart(requestDto, user);
        Apart savedApart = apartRepository.save(apart);
        ApartResponseDto responseDto = new ApartResponseDto(savedApart);
        return new CommonDto<>(HttpStatus.OK.value(), "아파트 생성에 성공하였습니다.", responseDto);
    }

    public CommonDto<ApartResponseDto> getApart(Long id) {

        QLike Like = QLike.like;

        Apart apart = apartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 아파트 ID입니다."));

        ApartResponseDto responseDto = new ApartResponseDto(apart);

        Long likesCount = jpaQueryFactory.select(Wildcard.count)
                .from(Like)
                .where(Like.apart.id.eq(apart.getId()))
                .fetchOne();

        responseDto.updateLikesCount(likesCount);

        return new CommonDto<>(HttpStatus.OK.value(), "아파트 조회에 성공하였습니다.", responseDto);
    }

    public CommonDto<List<ApartResponseDto>> getAparts(String area, int page) {

        QApart qApart = QApart.apart;

        var query = jpaQueryFactory.selectFrom(qApart)
                .orderBy(qApart.createdAt.desc());

        if (area != null) {
            query.where(qApart.area.eq(area));
        }

        List<Apart> apartList = query.offset(page * 5)
                .limit(5)
                .fetch();

        List<ApartResponseDto> apartResponseDtoList = apartList.stream()
                .map(ApartResponseDto::new)
                .toList();

        if (apartResponseDtoList.isEmpty()) {
            return new CommonDto<>(HttpStatus.NOT_FOUND.value(), "해당 지역의 아파트가 없습니다.", new ArrayList<>());
        }

        return new CommonDto<>(HttpStatus.OK.value(), (area == null || area.isEmpty() ? "모든" : area + " 지역별") + " 아파트 조회에 성공하였습니다.", apartResponseDtoList);
    }

    @Transactional
    public CommonDto<ApartResponseDto> updateApart(Long id, ApartRequestDto requestDto, User user) {

        Apart apart = apartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 아파트 ID입니다."));

        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (!apart.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("권한이 없는 사용자입니다.");
            }
        }

        apart.update(requestDto);
        Apart updatedApart = apartRepository.save(apart);
        ApartResponseDto responseDto = new ApartResponseDto(updatedApart);
        return new CommonDto<>(HttpStatus.OK.value(), "아파트 수정에 성공하였습니다.", responseDto);
    }

    public CommonDto<String> deleteApart(Long id, User user) {

        Apart apart = apartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 아파트 ID입니다."));

        if (!user.getRole().equals(UserRoleEnum.ADMIN)) {
            if (!apart.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("권한이 없는 사용자입니다.");
            }
        }

        apartRepository.delete(apart);
        return new CommonDto<>(HttpStatus.OK.value(), "아파트 삭제에 성공하였습니다.", null);
    }

    public CommonDto<List<ApartResponseDto>> getApartsByArea(String area, int page, int size, String sortBy, String order) {

        Pageable pageable = PageRequest.of(page, size, order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        Page<Apart> apartsPage = apartRepository.findByArea(area, pageable);
        List<ApartResponseDto> responseDtos = apartsPage.stream()
                .map(ApartResponseDto::new)
                .collect(Collectors.toList());
        return new CommonDto<>(HttpStatus.OK.value(), area + " 지역별 아파트 조회에 성공하였습니다.", responseDtos);
    }
}
