package com.phcworld.userservice.service;

import com.phcworld.userservice.infrastructure.UserEntity;
import com.phcworld.userservice.domain.port.LoginUserRequestDto;
import com.phcworld.userservice.controller.port.UserResponseDto;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.jwt.TokenProvider;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.infrastructure.UserJpaRepository;
import com.phcworld.userservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
//@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final TokenProvider tokenProvider;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public TokenDto login(LoginUserRequestDto requestUser) {
        // 비밀번호 확인 + spring security 객체 생성 후 JWT 토큰 생성
        Authentication authentication = SecurityUtil.getAuthentication(requestUser, userDetailsService, passwordEncoder);

        // 토큰 발급
        return tokenProvider.generateTokenDto(authentication);
    }

    public TokenDto getNewToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return tokenProvider.generateTokenDto(authentication);
    }

    public UserResponseDto getLoginUserInfo(){
        String userId = SecurityUtil.getCurrentMemberId();
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(NotFoundException::new);
        return UserResponseDto.of(user);
    }
}
