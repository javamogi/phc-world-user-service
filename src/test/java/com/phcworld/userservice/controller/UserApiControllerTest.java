package com.phcworld.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.port.UserRequestDto;
import com.phcworld.userservice.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Disabled("kafka 연동")
class UserApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void 회원가입_성공() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("abcdefg@test.test")
                .password("abcde")
                .name("에이비씨디이")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 회원가입_실패_중복_이메일() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("test@test.test")
                .password("abcde")
                .name("에이비씨디이")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void 회원가입_실패_모든_요소_빈값() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("")
                .password("")
                .name("")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.length()").value(6))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이메일_입력_없음() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("")
                .password("password")
                .name("name")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.length()").value(1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이메일_형식_아님() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("test")
                .password("password")
                .name("name")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.[0]").value("이메일 형식이 아닙니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_비밀번호_입력_없음() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("")
                .name("name")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_비밀번호_입력_최소입력_미만() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("tes")
                .name("name")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.[0]").value("비밀번호는 4자 이상으로 해야합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이름_입력_없음() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("test")
                .name("")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.length()").value(3))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이름_특수문자_입력() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("test")
                .name("!@#$$")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.[0]").value("이름은 한글, 영문, 숫자만 가능합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이름_최소입력_미만() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("test")
                .name("ab")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.[0]").value("이름은 영문 3자 이상 20자 이하 또는 한글 두자이상 6자 이하로 해야합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_이름_최대입력_초과() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("testttt@test.test")
                .password("tes")
                .name("aaaaabbbbbcccccddddde")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(jsonPath("$.messages.[0]").value("이름은 영문 3자 이상 20자 이하 또는 한글 두자이상 6자 이하로 해야합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 요청_회원_정보_가져오기() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(get("/users/{userId}", "3465335b-5457-4219-a0b2-d0c8b79d16ac")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("test2@test.test"))
                .andExpect(jsonPath("$.name").value("테스트2"))
                .andExpect(status().isOk());
    }

    @Test
    void 회원_정보_변경_성공() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        File file = new File("src/main/resources/static/image/PHC-WORLD.png");
        byte[] bytesFile = Files.readAllBytes(file.toPath());
        String imgData = Base64.getEncoder().encodeToString(bytesFile);

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .userId("a2240b59-47f6-4ad4-ba07-f7c495909f40")
                .email("test@test.test")
                .password("test")
                .name("테스트")
                .imageName("test.png")
                .imageData(imgData)
                .build();

        String request = objectMapper.writeValueAsString(userRequestDto);

        this.mvc.perform(patch("/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                        )
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.test"))
                .andExpect(jsonPath("$.name").value("테스트"))
                .andExpect(status().isOk());
    }

    @Test
    void 회원_정보_변경_실패_요청_회원_다름() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("1", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        String userId = UUID.randomUUID().toString();
        UserRequestDto requestDto = UserRequestDto.builder()
                .userId(userId)
                .email("test2@test.test")
                .password("test2")
                .name("test2")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(patch("/users")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원_정보_삭제_성공() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(delete("/users/{userId}", "a2240b59-47f6-4ad4-ba07-f7c495909f40")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("삭제 성공"))
                .andExpect(status().isOk());
    }

    @Test
    void 회원_정보_삭제_성공_관리자_권한() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(delete("/users/{userId}", "3465335b-5457-4219-a0b2-d0c8b79d16ac")
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 회원_정보_삭제_요청_회원_다름() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(delete("/users/{id}", 1L)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 요청_회원_목록_가져오기() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);

        List<String> ids = new ArrayList<>();
        ids.add("a2240b59-47f6-4ad4-ba07-f7c495909f40");
        ids.add("3465335b-5457-4219-a0b2-d0c8b79d16ac");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("userIds", ids);

        this.mvc.perform(get("/users")
                        .params(params)
                        .with(csrf())
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }
}