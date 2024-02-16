package com.phcworld.userservice.jwt;

import com.phcworld.userservice.exception.model.BadRequestException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer ";
//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 *24;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 3;  // 3일
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 10;  // 10초

    private final Environment env;

    public TokenDto generateTokenDto(Authentication authentication) {
        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = generateAccessToken(authentication, now);

        // Refresh Token 생성
        String refreshToken = generateRefreshToken(authentication, now);

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new UnauthorizedException();
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        return authentication;
    }

    private Claims parseClaims(String accessToken) {
        String secret = env.getProperty("jwt.secret");
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(keyBase64Encoded);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String generateAccessToken(Authentication authentication, long now){
        String secret = env.getProperty("jwt.secret");
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(keyBase64Encoded);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(userId)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication, long now){
        String secret = env.getProperty("jwt.secret");
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(keyBase64Encoded);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Refresh Token 생성
        return Jwts.builder()
                .setSubject(userId)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}