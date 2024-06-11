package com.phcworld.userservice.service;

import com.phcworld.userservice.domain.port.LoginUserRequestDto;
import com.phcworld.userservice.controller.port.UserResponseDto;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private LoginService loginService;

    @Test
    void 로그인_실패_가입되지_않은_이메일(){
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("test")
                .build();
        when(loginService.login(requestDto)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    void 로그인_실패_비밀번호_틀림(){
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("test1")
                .build();
        when(loginService.login(requestDto)).thenThrow(BadCredentialsException.class);
        Assertions.assertThrows(BadCredentialsException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    void 로그인_실패_삭제된_회원(){
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("test1")
                .build();
        when(loginService.login(requestDto)).thenThrow(DeletedEntityException.class);
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            loginService.login(requestDto);
        });
    }

    @Test
    void 로그인_성공(){
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("test")
                .build();
        TokenDto tokenDto = TokenDto.builder()
                .grantType("grantType")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        when(loginService.login(requestDto)).thenReturn(tokenDto);
        TokenDto resultToken = loginService.login(requestDto);
        assertThat(resultToken).isEqualTo(tokenDto);
    }

    @Test
    void 로그인_회원_정보_가져오기(){
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email("test@test.test")
                .name("테스트")
                .createDate("방금전")
                .build();
        when(loginService.getLoginUserInfo()).thenReturn(userResponseDto);
        UserResponseDto result = loginService.getLoginUserInfo();
        assertThat(result).isEqualTo(userResponseDto);
    }

}