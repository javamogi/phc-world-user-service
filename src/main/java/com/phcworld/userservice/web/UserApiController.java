package com.phcworld.userservice.web;

import com.phcworld.userservice.dto.LoginUserRequestDto;
import com.phcworld.userservice.dto.SuccessResponseDto;
import com.phcworld.userservice.dto.UserRequestDto;
import com.phcworld.userservice.dto.UserResponseDto;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final Environment env;

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("")
    public UserResponseDto create(@Valid @RequestBody UserRequestDto user) {
        return UserResponseDto.of(userService.registerUser(user));
    }

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginUserRequestDto user) {
        return userService.login(user);
    }

    @GetMapping("/userInfo")
    public UserResponseDto getUserInfo(){
        return userService.getLoginUserInfo();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserInfo(@PathVariable(name = "userId") String userId){
        return userService.getUserInfo(userId);
    }

    @PatchMapping("")
    public UserResponseDto updateUser(@RequestBody UserRequestDto requestDto){
        return userService.modifyUserInfo(requestDto);
    }

    @DeleteMapping("/{userId}")
    public SuccessResponseDto deleteUser(@PathVariable(name = "userId") String userId){
        return userService.deleteUser(userId);
    }

    @GetMapping("/logout")
    public String logout(){
        return userService.logout();
    }

    @GetMapping("/newToken")
    public TokenDto getToken(){
        return userService.getNewToken();
    }
}
