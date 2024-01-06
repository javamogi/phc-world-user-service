package com.phcworld.userservice.jwt;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.exception.model.BadRequestException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Authentication authentication;

    @BeforeEach
    void setAuthentication(){
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("test@test.test", "", authorities);
        authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    @Test
    public void 비밀키_암호화(){
        String secretKeyPlain = "spring-security-jwt-phc-world-secret-key";
        // 키를 Base64 인코딩
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        log.info("key : {}", keyBase64Encoded);
        log.info("secret key : {}", secretKey);
        assertThat(keyBase64Encoded).isEqualTo(secretKey);

        byte[] decodeByte = Base64.getDecoder().decode(keyBase64Encoded);
        String str = new String(decodeByte);
        log.info("str : {}", str);
        assertThat(str).isEqualTo(secretKeyPlain);
    }

    @Test
    void 토큰_생성(){
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);
        log.info("access token : {}", accessToken);
    }

//    @Test
//    void 토큰_검증_성공(){
//        long now = (new Date()).getTime();
//        String accessToken = tokenProvider.generateAccessToken(authentication, now);
//        boolean result = tokenProvider.validateToken(accessToken);
//        assertThat(result).isTrue();
//    }

//    @Test
//    void 토큰_검증_잘못된_토큰(){
//        long now = (new Date()).getTime();
//        String accessToken = tokenProvider.generateAccessToken(authentication, now);
//        String finalAccessToken = accessToken.replace(".", "");
//        Assertions.assertThrows(BadRequestException.class, () -> {
//            tokenProvider.validateToken(finalAccessToken);
//        });
//    }

//    @Test
//    void 토큰_검증_만료된_토큰(){
//        String accessToken = Jwts.builder()
//                .setExpiration(new Date(new Date().getTime()))
//                .compact();
//
//        Assertions.assertThrows(UnauthorizedException.class, () -> {
//            tokenProvider.validateToken(accessToken);
//        });
//    }

    @Test
    void 토큰_응답_dto_생성(){
        TokenDto dto = tokenProvider.generateTokenDto(authentication);
        log.info("token : {}", dto);
    }

    @Test
    void encodePassword(){
        String password = passwordEncoder.encode("test");
        log.info("password : {}", password);
        assertThat(passwordEncoder.matches("test", password)).isTrue();
    }

    @Test
    void test(){
        log.info("test : {}", UUID.randomUUID().toString());
    }

}