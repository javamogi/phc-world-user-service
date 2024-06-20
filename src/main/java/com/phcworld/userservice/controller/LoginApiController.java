package com.phcworld.userservice.controller;

import com.phcworld.userservice.controller.port.LoginService;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.LoginRequest;
import com.phcworld.userservice.controller.response.UserResponse;
import com.phcworld.userservice.jwt.dto.TokenDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Builder
public class LoginApiController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest user) {
        TokenDto token = loginService.login(user);
        return ResponseEntity
                .ok()
                .body(token);
    }

    @GetMapping("/newToken")
    public ResponseEntity<TokenDto> getNewToken(){
        TokenDto token = loginService.getNewToken();
        return ResponseEntity
                .ok()
                .body(token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @GetMapping("/userInfo")
    public ResponseEntity<UserResponse> getUserInfo(){
        User user = loginService.getLoginUserInfo();
        return ResponseEntity
                .ok()
                .body(UserResponse.of(user));
    }
}
