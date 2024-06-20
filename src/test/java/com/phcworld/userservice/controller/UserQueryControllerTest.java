package com.phcworld.userservice.controller;

import com.phcworld.userservice.controller.response.UserResponse;
import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.mock.FakeAuthentication;
import com.phcworld.userservice.mock.FakeLocalDateTimeHolder;
import com.phcworld.userservice.mock.TestContainer;
import com.phcworld.userservice.utils.LocalDateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserQueryControllerTest {

    @Test
    @DisplayName("회원 아이디로 회원정보 가져오기")
    void getUserInfo(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .name("테스트")
                .userId("1111")
                .password("test")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(time)
                .build());
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<UserResponse> result = testContainer.userQueryApiController.getUserInfo("1111");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().userId()).isEqualTo("1111");
        assertThat(result.getBody().email()).isEqualTo("test@test.test");
        assertThat(result.getBody().name()).isEqualTo("테스트");
        assertThat(result.getBody().createDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().profileImage())
                .isEqualTo("http://localhost:8080/image/" + "blank-profile-picture.png");
    }

    @Test
    @DisplayName("회원 정보 요청 실패 가입하지 않은 회원")
    void failedGetUserInfo(){
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.userQueryApiController.getUserInfo("1111");
        });
    }

    @Test
    @DisplayName("요청 회원 목록")
    void getListByList(){
        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        User user = User.builder()
                .id(1L)
                .email("test@test.test")
                .name("테스트")
                .userId("1111")
                .password("test")
                .isDeleted(false)
                .authority(Authority.ROLE_ADMIN)
                .profileImage("blank-profile-picture.png")
                .createDate(time)
                .updateDate(time)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("test2@test.test")
                .name("테스트2")
                .userId("2222")
                .password("test2")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(time)
                .updateDate(time)
                .build();
        testContainer.userRepository.save(user);
        testContainer.userRepository.save(user2);

        List<String> userIds = new ArrayList<>();
        userIds.add("1111");
        userIds.add("2222");

        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<Map<String, UserResponse>> result = testContainer.userQueryApiController.getUsers(userIds);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2)
                .containsKey("1111")
                .containsKey("2222");
    }

    @Test
    @DisplayName("회원 이름으로 회원정보 가져오기")
    void getUserByName(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("test@test.test")
                .name("테스트")
                .userId("1111")
                .password("test")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(time)
                .build());
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<List<UserResponse>> result = testContainer.userQueryApiController.getUsersByName("테스트");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).userId()).isEqualTo("1111");
        assertThat(result.getBody().get(0).email()).isEqualTo("test@test.test");
        assertThat(result.getBody().get(0).name()).isEqualTo("테스트");
        assertThat(result.getBody().get(0).createDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().get(0).profileImage())
                .isEqualTo("http://localhost:8080/image/" + "blank-profile-picture.png");
    }

}
