package com.phcworld.userservice.domain;

import com.phcworld.userservice.domain.port.UserRequest;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.service.port.LocalDateTimeHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public String getProfileImageUrl(){
        return "http://localhost:8080/image/" + profileImage;
    }

    public static User from(UserRequest request, PasswordEncoder passwordEncoder, LocalDateTimeHolder timeHolder) {
        return User.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .authority(Authority.ROLE_USER)
                .createDate(timeHolder.now())
                .updateDate(timeHolder.now())
                .profileImage("blank-profile-picture.png")
                .userId(UUID.randomUUID().toString())
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

    public User getUser(){
        return this;
    }
}
