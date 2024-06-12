package com.phcworld.userservice.mock;


import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.service.port.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeUserRepository implements UserRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<User> data = new ArrayList<>();
    @Override
    public Optional<User> findByEmail(String email) {
        return data.stream().filter(user -> user.getEmail().equals(email)).findAny();
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return data.stream().filter(user -> user.getUserId().equals(userId)).findAny();
    }

    @Override
    public List<User> findByUserIds(List<String> userIds) {
        return data.stream()
                .filter(user -> userIds.contains(user.getUserId()))
                .collect(Collectors.toList());
    }


    @Override
    public User save(User user) {
        if(user.getId() == null || user.getId().equals(0L)){
            User newUser = User.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .email(user.getEmail())
                    .userId(user.getUserId())
                    .name(user.getName())
                    .password(user.getPassword())
                    .profileImage(user.getProfileImage())
                    .createDate(user.getCreateDate())
                    .updateDate(user.getUpdateDate())
                    .authority(user.getAuthority())
                    .isDeleted(user.isDeleted())
                    .build();
            data.add(newUser);
            return newUser;
        } else {
            data.removeIf(u -> Objects.equals(u.getId(), user.getId()));
            data.add(user);
            return user;
        }
    }
}
