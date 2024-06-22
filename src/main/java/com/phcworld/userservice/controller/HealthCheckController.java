package com.phcworld.userservice.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final Environment env;

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status(){
        return String.format("It's Working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }
}
