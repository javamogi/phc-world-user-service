package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.UserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    User register(UserRequest request);
    User getUser(String userId);
    User modify(UserRequest request);
    User delete(String userId);
    Map<String, User> getUsers(List<String> userIds);
}
