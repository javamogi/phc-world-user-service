package com.phcworld.userservice.service;

import com.phcworld.userservice.controller.port.LoginService;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.LoginRequest;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.security.utils.SecurityUtil;
import com.phcworld.userservice.service.port.TokenProvider;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    public TokenDto login(LoginRequest request) {
        // 비밀번호 확인 + spring security 객체 생성 후 JWT 토큰 생성
        Authentication authentication = SecurityUtil.getAuthentication(request, userDetailsService, passwordEncoder);

        // 토큰 발급
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public TokenDto getNewToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public User getLoginUserInfo(){
        String userId = SecurityUtil.getCurrentMemberId();
        return userRepository.findByUserId(userId)
                .orElseThrow(NotFoundException::new);
    }
}
