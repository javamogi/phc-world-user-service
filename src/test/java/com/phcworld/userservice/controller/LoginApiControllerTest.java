package com.phcworld.userservice.controller;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.LoginRequest;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.mock.FakeAuthentication;
import com.phcworld.userservice.mock.TestContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginApiControllerTest {

    @Test
    @DisplayName("회원은 로그인을 할 수 있다")
    void successLogin(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .userId("1111")
                .name("테스트")
                .password("test")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
        LoginRequest requestDto = LoginRequest.builder()
                .email("test@test.test")
                .password("test")
                .build();
        Authentication authentication = new FakeAuthentication(1, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<TokenDto> result = testContainer.loginApiController.login(requestDto);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getBody().getGrantType()).isEqualTo("bearer");
        assertThat(result.getBody().getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("로그인 실패 가입되지 않은 이메일")
    void failedLoginWhenNotFoundEmail(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        LoginRequest requestDto = LoginRequest.builder()
                .email("test2@test.test")
                .password("test2")
                .build();

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("로그인_실패_비밀번호_틀림")
    void failedLoginWhenNotMatchPassword(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .userId("1111")
                .name("테스트")
                .password("test")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
        LoginRequest requestDto = LoginRequest.builder()
                .email("test@test.test")
                .password("testtest")
                .build();

        // when
        // then
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            testContainer.loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("로그인_실패_삭제된_회원")
    void failedLoginWhenDeletedUser(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .userId("1111")
                .name("테스트")
                .password("test")
                .isDeleted(true)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
        LoginRequest requestDto = LoginRequest.builder()
                .email("test@test.test")
                .password("test")
                .build();

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            testContainer.loginService.login(requestDto);
        });
    }

    @Test
    @DisplayName("회원은 토큰이 만료되었을 때 새로운 토큰을 발행받을 수 있다")
    void successNewToken(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        Authentication authentication = new FakeAuthentication(1, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<TokenDto> result = testContainer.loginApiController.getNewToken();

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getBody().getGrantType()).isEqualTo("bearer");
        assertThat(result.getBody().getAccessToken()).isEqualTo("accessToken");
    }
}