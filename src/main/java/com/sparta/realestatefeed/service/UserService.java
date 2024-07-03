package com.sparta.realestatefeed.service;

import com.sparta.realestatefeed.dto.UserRegisterRequestDto;
import com.sparta.realestatefeed.dto.UserRegisterResponseDto;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.exception.UserAlreadyExistsException;
import com.sparta.realestatefeed.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.InputMismatchException;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto userRegisterRequestDto) {

        if (userRepository.existsByUserName(userRegisterRequestDto.getUserName())) {
            throw new UserAlreadyExistsException("이미 존재하는 ID");
        }

        if (userRegisterRequestDto.getPassword().length() < 8 || userRegisterRequestDto.getPassword().length() > 15) {
            throw new InputMismatchException("잘못된 비밀번호 형식");
        }

        User user = initialize(userRegisterRequestDto);
        userRepository.save(user);

        return new UserRegisterResponseDto(user);
    }

    private User initialize(UserRegisterRequestDto userRegisterRequestDto) {

        User user = new User(userRegisterRequestDto);
        user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));

        return user;
    }
}
