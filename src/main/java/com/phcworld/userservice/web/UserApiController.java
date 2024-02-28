package com.phcworld.userservice.web;

import com.phcworld.userservice.dto.SuccessResponseDto;
import com.phcworld.userservice.dto.UserRequestDto;
import com.phcworld.userservice.dto.UserResponseDto;
import com.phcworld.userservice.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final Environment env;

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status(){
        return String.format("It's Working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "가입된 이메일")
    })
    @PostMapping("")
    public UserResponseDto create(@Valid @RequestBody UserRequestDto user) {
        return UserResponseDto.of(userService.registerUser(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @GetMapping("/{userId}")
    public UserResponseDto getUserInfo(@PathVariable(name = "userId") String userId){
        return userService.getUserInfo(userId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @PatchMapping("")
    public UserResponseDto updateUser(@RequestBody UserRequestDto requestDto){
        return userService.modifyUserInfo(requestDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @DeleteMapping("/{userId}")
    public SuccessResponseDto deleteUser(@PathVariable(name = "userId") String userId){
        return userService.deleteUser(userId);
    }

    @GetMapping("")
    public Map<String, UserResponseDto> getUsers(@RequestParam(value = "userIds") List<String> userIds){
        return userService.getUsersByUserIdList(userIds);
    }
}
