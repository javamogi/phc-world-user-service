package com.phcworld.userservice.domain;

import com.phcworld.userservice.domain.port.UserRequest;
import com.phcworld.userservice.mock.FakeLocalDateTimeHolder;
import com.phcworld.userservice.mock.FakePasswordEncode;
import com.phcworld.userservice.mock.TestUuidHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("UserRequest 요청 정보로 생성할 수 있다.")
    void createByUserRequest(){
        // given
        UserRequest userRequest = UserRequest.builder()
                .email("test@test.test")
                .password("test")
                .name("테스트")
                .build();
        LocalDateTime now = LocalDateTime.now();

        // when
        User user = User.from(userRequest,
                new FakePasswordEncode(),
                new FakeLocalDateTimeHolder(now),
                new TestUuidHolder("test-1234"));

        // then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("test@test.test");
        assertThat(user.getPassword()).isEqualTo("test");
        assertThat(user.getUserId()).isEqualTo("test-1234");
        assertThat(user.getName()).isEqualTo("테스트");
        assertThat(user.getAuthority()).isEqualTo(Authority.ROLE_USER);
        assertThat(user.getCreateDate()).isEqualTo(now);
        assertThat(user.getUpdateDate()).isEqualTo(now);
        assertThat(user.getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("UserRequest 요청 정보로 수정할 수 있다.")
    void modifyByUserRequest(){
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("test@test.test")
                .password("test")
                .userId("1111")
                .name("테스트")
                .profileImage("blank-profile-picture.png")
                .createDate(now)
                .updateDate(now)
                .authority(Authority.ROLE_USER)
                .isDeleted(false)
                .build();
        UserRequest userRequest = UserRequest.builder()
                .userId("1111")
                .password("test2")
                .name("테스트2")
                .build();

        // when
        user = user.modify(userRequest, "test.png", new FakePasswordEncode(), new FakeLocalDateTimeHolder(now));

        // then
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("test@test.test");
        assertThat(user.getPassword()).isEqualTo("test2");
        assertThat(user.getUserId()).isEqualTo("1111");
        assertThat(user.getName()).isEqualTo("테스트2");
        assertThat(user.getAuthority()).isEqualTo(Authority.ROLE_USER);
        assertThat(user.getProfileImage()).isEqualTo("test.png");
        assertThat(user.getCreateDate()).isEqualTo(now);
        assertThat(user.getUpdateDate()).isEqualTo(now);
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("삭제 요청으로 삭제 값이 변경된다.")
    void delete(){
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("test@test.test")
                .password("test")
                .userId("1111")
                .name("테스트")
                .profileImage("blank-profile-picture.png")
                .createDate(now)
                .updateDate(now)
                .authority(Authority.ROLE_USER)
                .isDeleted(false)
                .build();

        // when
        user.delete();

        // then
        assertThat(user.isDeleted()).isTrue();
    }

}