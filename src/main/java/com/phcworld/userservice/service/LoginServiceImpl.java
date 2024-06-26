package com.phcworld.userservice.service;

import com.phcworld.userservice.controller.port.LoginService;
import com.phcworld.userservice.domain.LoginRequest;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.security.utils.SecurityUtil;
import com.phcworld.userservice.service.port.TokenProvider;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Builder
public class LoginServiceImpl implements LoginService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public LoginServiceImpl(TokenProvider tokenProvider,
//                            @Qualifier("jpaUserRepository") UserRepository userRepository,
                            @Qualifier("redisUserRepository") UserRepository userRepository,
                            AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenDto login(LoginRequest request) {
        // 비밀번호 확인 + spring security 객체 생성 후 JWT 토큰 생성
        Authentication authentication
                = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.email(), request.password())
                );

        // 토큰 발급
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    public TokenDto getNewToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return tokenProvider.generateTokenDto(authentication);
    }

    @Override
    @Transactional(readOnly = true)
    public User getLoginUserInfo(){
        String userId = SecurityUtil.getCurrentMemberId();
        return userRepository.findByUserId(userId)
                .orElseThrow(NotFoundException::new);
    }
}
