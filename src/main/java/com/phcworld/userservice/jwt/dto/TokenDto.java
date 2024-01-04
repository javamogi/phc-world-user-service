package com.phcworld.userservice.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
