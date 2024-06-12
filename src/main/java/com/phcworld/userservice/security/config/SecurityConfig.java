package com.phcworld.userservice.security.config;

import com.phcworld.userservice.jwt.config.JwtSecurityConfig;
import com.phcworld.userservice.jwt.entry.JwtAuthenticationEntryPoint;
import com.phcworld.userservice.jwt.filter.JwtExceptionFilter;
import com.phcworld.userservice.jwt.handler.JwtAccessDeniedHandler;
import com.phcworld.userservice.service.port.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final Environment env;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2, css, js 무시
    @Bean
    public WebSecurityCustomizer configure(){
        return web -> web.ignoring()
                .requestMatchers(
                        /* swagger v2 */
                        "/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        /* swagger v3 */
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                )
                .requestMatchers(
                "/h2-console/**",
                "/favicon.ico",
                        "/image/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(authorizeRequestsConfig ->
                        authorizeRequestsConfig
//                                .requestMatchers("/",
//                                        "/users",
//                                        "/users/login",
//                                        "/actuator/**").permitAll()
                                .requestMatchers("/**").permitAll()
//                                .requestMatchers("/**")
//                                .hasIpAddress(env.getProperty("gateway.ip"))
                                .anyRequest().authenticated()
                )
                // enable h2-console
                .headers(headers->
                        headers.contentTypeOptions(contentTypeOptionsConfig ->
                                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                .exceptionHandling(exceptionConfig ->
                        exceptionConfig
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler))

                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .sessionManagement(sessionManagementConfig -> sessionManagementConfig.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .with(new JwtSecurityConfig(tokenProvider, jwtExceptionFilter), Customizer.withDefaults())
                .build();

    }

}
