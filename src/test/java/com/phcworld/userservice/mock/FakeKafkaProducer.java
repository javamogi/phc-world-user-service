package com.phcworld.userservice.mock;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.service.port.UserProducer;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeKafkaProducer implements UserProducer {

    private final UserRepository userRepository;

    @Override
    public User send(String topic, User user) {
        return userRepository.save(user);
    }
}
