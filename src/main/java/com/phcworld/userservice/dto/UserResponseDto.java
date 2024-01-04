package com.phcworld.userservice.dto;

import com.phcworld.userservice.domain.User;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String email,
        String name,
        String createDate,
        String profileImage
) {
    public static UserResponseDto of(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createDate(user.getFormattedCreateDate())
//                .profileImage(user.getProfileImageData())
                .profileImage(user.getProfileImageUrl())
                .build();
    }
}
