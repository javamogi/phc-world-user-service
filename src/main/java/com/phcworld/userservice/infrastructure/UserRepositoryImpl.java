package com.phcworld.userservice.infrastructure;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return userJpaRepository.findByUserId(userId)
                .map(UserEntity::toModel);
    }

    @Override
    public List<User> findByUserIds(List<String> userIds) {
        return userJpaRepository.findByUserIds(userIds)
                .stream()
                .map(UserEntity::toModel)
                .toList();
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public List<User> findByName(String name) {
        return userJpaRepository.findByName(name)
                .stream()
                .map(UserEntity::toModel)
                .toList();
    }
}
