package com.phcworld.userservice.service.port;

import com.phcworld.userservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    List<User> findByUserIds(List<String> userIds);
    User save(User user);

    List<User> findByName(String name);
}
