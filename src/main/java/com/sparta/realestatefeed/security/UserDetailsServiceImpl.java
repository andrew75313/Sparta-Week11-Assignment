package com.sparta.realestatefeed.security;

import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다." + username));

        return new UserDetailsImpl(user);
    }
}
