package com.phcworld.userservice.controller;

import com.phcworld.userservice.controller.port.UserResponse;
import com.phcworld.userservice.controller.port.UserService;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.UserRequest;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.of(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable(name = "userId") String userId){
        User user = userService.getUser(userId);
        return ResponseEntity
                .ok()
                .body(UserResponse.of(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @PatchMapping("")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest request){
        User user = userService.modify(request);
        return ResponseEntity
                .ok()
                .body(UserResponse.of(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음"),
            @ApiResponse(responseCode = "409", description = "이미 삭제된 회원")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable(name = "userId") String userId){
        User user = userService.delete(userId);
        return ResponseEntity
                .ok()
                .body(UserResponse.of(user));
    }

    @GetMapping("")
    public ResponseEntity<Map<String, UserResponse>> getUsers(@RequestParam(value = "userIds") List<String> userIds){
        Map<String, User> map = userService.getUsers(userIds);
        Map<String, UserResponse> users = new HashMap<>();
        for (String userId : map.keySet()){
            users.put(userId, UserResponse.of(map.get(userId)));
        }
        return ResponseEntity
                .ok()
                .body(users);
    }
}
