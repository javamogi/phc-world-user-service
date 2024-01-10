package com.phcworld.userservice.dto;

import com.phcworld.userservice.domain.User;
import lombok.Builder;

@Builder
public record UserResponseDto(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
    public static UserResponseDto of(User user){
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
