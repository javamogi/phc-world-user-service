package com.phcworld.userservice.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.userservice.exception.model.CustomBaseException;
import com.phcworld.userservice.exception.model.ErrorCode;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomBaseException e){
            ErrorCode error = e.getErrorCode();
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(error.getHttpStatus().value());
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String errorMessage = objectMapper.writeValueAsString(e.getErrorCode().getMessage());
            response.getWriter().write(errorMessage);
        } catch (WeakKeyException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("잘못된 토큰입니다.");
        }
    }
}
