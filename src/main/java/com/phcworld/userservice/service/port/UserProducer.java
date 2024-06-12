package com.phcworld.userservice.service.port;

import com.phcworld.userservice.domain.User;

public interface UserProducer {
    User send(String topic, User user);
}
