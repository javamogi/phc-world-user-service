package com.phcworld.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.port.LoginUserRequestDto;
import com.phcworld.userservice.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void 회원_로그인_실패_비밀번호_틀림() throws Exception {
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("testt")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.error").value("잘못된 요청입니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원_로그인_실패_없는_이메일() throws Exception {
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("testtest@test.test")
                .password("test")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.error").value("존재하지 않는 엔티티입니다."))
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원_로그인_실패_삭제된_회원() throws Exception {
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test3@test.test")
                .password("test3")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void 로그인_성공() throws Exception {
        LoginUserRequestDto requestDto = LoginUserRequestDto.builder()
                .email("test@test.test")
                .password("test")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 로그인_회원_정보_가져오기() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(get("/users/userInfo")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(jsonPath("$.email").value("test@test.test"))
                .andExpect(jsonPath("$.name").value("테스트"))
                .andExpect(status().isOk());
    }

    @Test
    void 새로운_토큰_발행() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String refreshToken = tokenProvider.generateRefreshToken(authentication, now);

        this.mvc.perform(get("/users/newToken")
                        .with(csrf())
                        .header("Authorization", "Bearer " + refreshToken))
                .andDo(print())
                .andExpect(status().isOk());
    }


}