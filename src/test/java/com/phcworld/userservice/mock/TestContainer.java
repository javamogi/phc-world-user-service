package com.phcworld.userservice.mock;

import com.phcworld.userservice.controller.LoginApiController;
import com.phcworld.userservice.controller.UserApiController;
import com.phcworld.userservice.controller.port.LoginService;
import com.phcworld.userservice.jwt.service.CustomUserDetailsService;
import com.phcworld.userservice.service.LoginServiceImpl;
import com.phcworld.userservice.service.UserServiceImpl;
import com.phcworld.userservice.service.port.LocalDateTimeHolder;
import com.phcworld.userservice.service.port.TokenProvider;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.Builder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestContainer {
    public final UserApiController userApiController;

    public final LoginService loginService;

    public final LoginApiController loginApiController;

    public final PasswordEncoder passwordEncoder;

    public final UserRepository userRepository;

    public final TokenProvider tokenProvider;

    public final UserDetailsService userDetailsService;

    public final AuthenticationManager authenticationManager;

    @Builder
    public TestContainer(LocalDateTimeHolder localDateTimeHolder){
        this.passwordEncoder = new FakePasswordEncode();
        this.userRepository = new FakeUserRepository();
        this.tokenProvider = new FakeTokenProvider();
        this.userDetailsService = new CustomUserDetailsService(userRepository);
        FakeAuthenticationProvider fakeAuthenticationProvider = new FakeAuthenticationProvider(
                userDetailsService, passwordEncoder);
        this.authenticationManager =  new FakeAuthenticationManager(fakeAuthenticationProvider);


        UserServiceImpl userService = UserServiceImpl.builder()
                .passwordEncoder(passwordEncoder)
                .userRepository(userRepository)
                .localDateTimeHolder(localDateTimeHolder)
                .build();
        this.userApiController = UserApiController.builder()
                .userService(userService)
                .build();
        this.loginService = LoginServiceImpl.builder()
                .authenticationManager(authenticationManager)
                .tokenProvider(tokenProvider)
                .build();
        this.loginApiController = LoginApiController.builder()
                .loginService(loginService)
                .build();
    }

}
