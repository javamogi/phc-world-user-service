package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.UserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    User register(UserRequest request);
    User getUserByUserId(String userId);
    User modify(UserRequest request);
    User delete(String userId);
    Map<String, User> getUsers(List<String> userIds);

    List<User> getUserByName(String name);
}
