package com.phcworld.userservice.domain;

import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.service.port.LocalDateTimeHolder;
import com.phcworld.userservice.service.port.UuidHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String userId;
    private String name;
    private Authority authority;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String profileImage;
    private boolean isDeleted;

    public static User from(User user, UuidHolder uuidHolder) {
        return User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .authority(user.getAuthority())
                .createDate(user.getCreateDate())
                .updateDate(user.getUpdateDate())
                .profileImage(user.getProfileImage())
                .userId(uuidHolder.random())
                .isDeleted(user.isDeleted)
                .build();
    }

    public String getProfileImageUrl(){
        return "http://localhost:8080/image/" + profileImage;
    }

    public static User from(UserRequest request,
                            PasswordEncoder passwordEncoder,
                            LocalDateTimeHolder timeHolder,
                            UuidHolder uuidHolder) {
        return User.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .authority(Authority.ROLE_USER)
                .createDate(timeHolder.now())
                .updateDate(timeHolder.now())
                .profileImage("blank-profile-picture.png")
                .userId(uuidHolder.random())
                .isDeleted(false)
                .build();
    }

    public User modify(UserRequest request,
                       String profileImg,
                       PasswordEncoder passwordEncoder,
                       LocalDateTimeHolder timeHolder) {
        return User.builder()
                .id(id)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .userId(userId)
                .name(request.name())
                .authority(authority)
                .createDate(createDate)
                .updateDate(timeHolder.now())
                .profileImage(profileImg)
                .isDeleted(isDeleted)
                .build();
    }

    public User delete() {
        if(this.isDeleted) {
            throw new DeletedEntityException();
        }
        this.isDeleted = true;
        return User.builder()
                .id(id)
                .email(email)
                .password(email)
                .userId(userId)
                .name(name)
                .authority(authority)
                .createDate(createDate)
                .updateDate(updateDate)
                .profileImage(profileImage)
                .isDeleted(isDeleted)
                .build();
    }

}
