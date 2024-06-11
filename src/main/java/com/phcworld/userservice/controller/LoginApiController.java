package com.phcworld.userservice.controller;

import com.phcworld.userservice.domain.port.LoginUserRequestDto;
import com.phcworld.userservice.controller.port.UserResponseDto;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.service.LoginService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class LoginApiController {

    private final LoginService loginService;

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginUserRequestDto user) {
        return loginService.login(user);
    }

    @GetMapping("/newToken")
    public TokenDto getToken(){
        return loginService.getNewToken();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @GetMapping("/userInfo")
    public UserResponseDto getUserInfo(){
        return loginService.getLoginUserInfo();
    }
}
