package com.phcworld.userservice.controller.port;

import com.phcworld.userservice.infrastructure.UserEntity;
import lombok.Builder;

@Builder
public record UserResponseDto(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
    public static UserResponseDto of(UserEntity user){
        return UserResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .createDate(user.getFormattedCreateDate())
//                .profileImage(user.getProfileImageData())
                .profileImage(user.getProfileImageUrl())
                .userId(user.getUserId())
                .build();
    }
}
