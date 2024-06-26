package com.phcworld.userservice.infrastructure;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRedisEntity implements Serializable {
    private String email;
    private String name;
    private String createDate;
    private String profileImage;
    private String userId;
    private boolean isDelete;
    private Authority authority;
    private String password;

    public static UserRedisEntity from(User user) {
        return UserRedisEntity.builder()
                .email(user.getEmail())
                .name(user.getName())
                .createDate(user.getCreateDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")))
                .profileImage(user.getProfileImage())
                .userId(user.getUserId())
                .isDelete(user.isDeleted())
                .authority(user.getAuthority())
                .password(user.getPassword())
                .build();
    }

    public User toModel() {
        return User.builder()
                .email(email)
                .name(name)
                .createDate(LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")))
                .profileImage(profileImage)
                .userId(userId)
                .isDeleted(isDelete)
                .authority(authority)
                .password(password)
                .build();
    }
}
