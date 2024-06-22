package com.phcworld.userservice.controller;

import com.phcworld.userservice.controller.port.UserService;
import com.phcworld.userservice.controller.response.UserResponse;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.UserRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Builder
public class UserCommandApiController {

    private final UserService userService;

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
}
