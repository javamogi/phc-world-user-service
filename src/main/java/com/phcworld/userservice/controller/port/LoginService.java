package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.LoginRequest;
import com.phcworld.userservice.jwt.dto.TokenDto;

public interface LoginService {
    public TokenDto login(LoginRequest request);
    public TokenDto getNewToken();
    public User getLoginUserInfo();
}
