package com.phcworld.userservice.service;

import com.phcworld.userservice.controller.port.LoginService;
import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.LoginRequest;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.jwt.service.CustomUserDetailsService;
import com.phcworld.userservice.mock.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginServiceImplTest {

    private LoginService loginService;

    @BeforeEach
    void init(){
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakeTokenProvider fakeTokenProvider = new FakeTokenProvider();
        FakePasswordEncode fakePasswordEncode = new FakePasswordEncode();
        FakeAuthenticationProvider fakeAuthenticationProvider = new FakeAuthenticationProvider(
                new CustomUserDetailsService(fakeUserRepository), fakePasswordEncode);
        FakeAuthenticationManager fakeAuthenticationManager = new FakeAuthenticationManager(fakeAuthenticationProvider);
        this.loginService = LoginServiceImpl.builder()
                .authenticationManager(fakeAuthenticationManager)
                .userRepository(fakeUserRepository)
                .tokenProvider(fakeTokenProvider)
                .build();

        fakeUserRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .password("test")
                .userId("1111")
                .name("테스트")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("test2@test.test")
                .userId("2222")
                .name("테스트2")
                .password("test2")
                .isDeleted(true)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
    }

    @Test
    @DisplayName("로그인 실패 가입되지 않은 이메일")
    void failedLoginWhenNotFoundEmail(){
        // given
        LoginRequest requestDto = LoginRequest.builder()
                .email("test3@test.test")
                .password("test3")
                .build();

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("로그인_실패_비밀번호_틀림")
    void failedLoginWhenNotMatchPassword(){
        // given
        LoginRequest requestDto = LoginRequest.builder()
                .email("test@test.test")
                .password("test1")
                .build();

        // when
        // then
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("로그인_실패_삭제된_회원")
    void failedLoginWhenDeletedUser(){
        // given
        LoginRequest requestDto = LoginRequest.builder()
                .email("test2@test.test")
                .password("test2")
                .build();

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("로그인_성공")
    void successLogin(){
        // given
        LoginRequest requestDto = LoginRequest.builder()
                .email("test@test.test")
                .password("test")
                .build();

        // when
        TokenDto result = loginService.login(requestDto);

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getGrantType()).isEqualTo("bearer");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("새토큰 발행")
    void successNewToken(){
        // given
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        TokenDto result = loginService.getNewToken();

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getGrantType()).isEqualTo("bearer");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
    }

}