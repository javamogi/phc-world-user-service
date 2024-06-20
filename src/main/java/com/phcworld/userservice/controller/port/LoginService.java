package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.LoginRequest;
import com.phcworld.userservice.jwt.dto.TokenDto;

public interface LoginService {
    TokenDto login(LoginRequest request);
    TokenDto getNewToken();
    User getLoginUserInfo();
}
