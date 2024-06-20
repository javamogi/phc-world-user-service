package com.phcworld.userservice.controller;

import com.phcworld.userservice.controller.port.UserService;
import com.phcworld.userservice.controller.response.UserResponse;
import com.phcworld.userservice.domain.User;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Builder
public class UserQueryApiController {

    private final UserService userService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청한 회원 정보 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable(name = "userId") String userId){
        User user = userService.getUserByUserId(userId);
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

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> getUsersByName(@RequestParam(name = "name") String name){
        List<User> users = userService.getUserByName(name);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::of)
                .toList();
        return ResponseEntity
                .ok()
                .body(responses);
    }
}
