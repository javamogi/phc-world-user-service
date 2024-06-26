package com.phcworld.userservice.infrastructure;

import com.phcworld.userservice.domain.User;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
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

    public static UserRedisEntity from(User user) {
        return UserRedisEntity.builder()
                .email(user.getEmail())
                .name(user.getName())
                .createDate(user.getCreateDate()
                        .withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .profileImage(user.getProfileImage())
                .userId(user.getUserId())
                .isDelete(user.isDeleted())
                .build();
    }

}
