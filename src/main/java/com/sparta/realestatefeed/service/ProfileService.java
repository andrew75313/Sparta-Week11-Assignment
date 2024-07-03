package com.sparta.realestatefeed.service;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.realestatefeed.dto.PasswordRequestDto;
import com.sparta.realestatefeed.dto.ProfileRequestDto;
import com.sparta.realestatefeed.dto.ProfileResponseDto;
import com.sparta.realestatefeed.entity.*;
import com.sparta.realestatefeed.exception.PasswordMismatchException;
import com.sparta.realestatefeed.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory jpaQueryFactory;

    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder, JPAQueryFactory jpaQueryFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public ProfileResponseDto getUserProfile(String userName) {

        QUser qUser = QUser.user;
        QLike qLike = QLike.like;

        User user = jpaQueryFactory.selectFrom(qUser)
                .where(qUser.userName.eq(userName))
                .fetchOne();

        if (user == null) {
            throw new IllegalArgumentException("해당 유저는 존재하지 않습니다.");
        }

        ProfileResponseDto profileResponseDto = new ProfileResponseDto(user);

        Long aptLikesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.user.id.eq(user.getId())
                        .and(qLike.apart.isNotNull()))
                .fetchOne();

        Long qnaLikesCount = jpaQueryFactory.select(Wildcard.count)
                .from(qLike)
                .where(qLike.user.id.eq(user.getId())
                        .and(qLike.qna.isNotNull()))
                .fetchOne();

        profileResponseDto.updateCounts(aptLikesCount, qnaLikesCount);

        return profileResponseDto;
    }

    public ProfileResponseDto updateUserProfile(String userName, ProfileRequestDto profileRequestDto) {

        User user = userRepository.findByUserName(userName).orElseThrow(
                () -> new IllegalArgumentException("해당 유저는 존재하지 않습니다.")
        );

        user.updateProfile(profileRequestDto);

        userRepository.save(user);

        return new ProfileResponseDto(user);
    }

    public void updateUserPassword(String userName, PasswordRequestDto passwordRequestDto) {

        User user = userRepository.findByUserName(userName).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

        if (passwordRequestDto.isPasswordMatching()) {

            throw new PasswordMismatchException("New Password equal Current Password");
        }


        if (!passwordRequestDto.isNewPasswordMatch()) {

            throw new PasswordMismatchException("New Password not equal Check New Password");
        }


        if (!passwordEncoder.matches(passwordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        List<String> previousPasswords = user.getPreviousPasswords();
        if (previousPasswords.stream().anyMatch(password -> passwordEncoder.matches(passwordRequestDto.getNewPassword(), password))) {
            throw new IllegalArgumentException("New password must be different from the last three passwords");
        }


        previousPasswords.add(0, user.getPassword());
        if (previousPasswords.size() > 3) {
            previousPasswords.remove(3);
        }

        user.setPassword(passwordEncoder.encode(passwordRequestDto.getNewPassword()));
        userRepository.save(user);

    }

}
