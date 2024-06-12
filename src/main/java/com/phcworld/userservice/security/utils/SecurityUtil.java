package com.phcworld.userservice.security.utils;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.port.LoginRequest;
import com.phcworld.userservice.jwt.service.CustomAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {

    public static String getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        return authentication.getName();
    }

    public static Authority getAuthorities() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Authority.valueOf(authorities);
    }

    public static boolean matchUserId(String userId) {
        return getCurrentMemberId().equals(userId);
    }

    public static boolean matchAdminAuthority() {
        return getAuthorities() != Authority.ROLE_ADMIN;
    }
}
