package com.phcworld.userservice.mock;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.service.port.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public class FakeTokenProvider implements TokenProvider {

    @Override
    public boolean validateToken(String token) {
        return token.equals("token");
    }

    @Override
    public TokenDto generateTokenDto(Authentication authentication) {
        return TokenDto.builder()
                .accessToken("accessToken")
                .grantType("bearer")
                .refreshToken("refreshToken")
                .build();
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(Authority.ROLE_USER.toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal =  new User(
                accessToken,
                "test",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    @Override
    public String generateAccessToken(Authentication authentication, long now) {
        return "accessToken";
    }

    @Override
    public String generateRefreshToken(Authentication authentication, long now) {
        return "refreshToken";
    }
}
