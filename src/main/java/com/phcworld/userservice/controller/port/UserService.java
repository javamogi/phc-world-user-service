package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.UserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User register(UserRequest request);
    public User getUser(String userId);
    public User modify(UserRequest request);
    public User delete(String userId);
    public Map<String, User> getUsers(List<String> userIds);
}
