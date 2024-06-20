package com.phcworld.userservice.controller.response;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.utils.LocalDateTimeUtils;
import lombok.Builder;

@Builder
public record UserResponse(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId,

        Boolean isDeleted
) {

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .createDate(LocalDateTimeUtils.getTime(user.getCreateDate()))
                .profileImage(user.getProfileImageUrl())
                .userId(user.getUserId())
                .isDeleted(user.isDeleted())
                .build();
    }
}
