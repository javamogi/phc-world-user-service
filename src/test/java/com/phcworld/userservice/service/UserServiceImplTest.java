package com.phcworld.userservice.service;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.UserRequest;
import com.phcworld.userservice.exception.model.*;
import com.phcworld.userservice.mock.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceImplTest {

    private UserServiceImpl userService;

    @BeforeEach
    void init(){
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakePasswordEncode fakePasswordEncode = new FakePasswordEncode();
        FakeLocalDateTimeHolder fakeLocalDateTimeHolder = new FakeLocalDateTimeHolder(
                LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111));
        TestUuidHolder testUuidHolder = new TestUuidHolder("12345");
        FakeKafkaProducer fakeKafkaProducer = new FakeKafkaProducer(fakeUserRepository);
        this.userService = UserServiceImpl.builder()
                .userRepository(fakeUserRepository)
                .passwordEncoder(fakePasswordEncode)
                .localDateTimeHolder(fakeLocalDateTimeHolder)
                .uuidHolder(testUuidHolder)
                .userProducer(fakeKafkaProducer)
                .build();

        fakeUserRepository.save(User.builder()
                        .id(1L)
                        .email("test@test.test")
                        .name("테스트")
                        .userId("1111")
                        .password("test")
                        .isDeleted(false)
                        .authority(Authority.ROLE_ADMIN)
                        .profileImage("blank-profile-picture.png")
                        .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                        .updateDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                        .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("test2@test.test")
                .name("테스트2")
                .userId("2222")
                .password("test2")
                .isDeleted(true)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
        fakeUserRepository.save(User.builder()
                .id(3L)
                .email("test3@test.test")
                .name("테스트3")
                .userId("3333")
                .password("test2")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111))
                .build());
    }

    @Test
    @DisplayName("회원가입 성공")
    void register() {
        // given
        UserRequest requestDto = UserRequest.builder()
                .email("test4@test.test")
                .password("test4")
                .name("테스트4")
                .build();

        // when
        User result = userService.register(requestDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test4@test.test");
        assertThat(result.getPassword()).isEqualTo("test4");
        assertThat(result.getUserId()).isEqualTo("12345");
        assertThat(result.getName()).isEqualTo("테스트4");
        assertThat(result.getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(result.getAuthority()).isEqualTo(Authority.ROLE_USER);
        assertThat(result.getCreateDate()).isEqualTo(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111));
        assertThat(result.getUpdateDate()).isEqualTo(LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111));
    }

    @Test
    @DisplayName("회원가입 실패 가입된 이메일")
    void failedRegisterWhenDuplicateEmail(){
        // given
        UserRequest requestDto = UserRequest.builder()
                .email("test@test.test")
                .password("test")
                .name("test")
                .build();

        // when
        // then
        Assertions.assertThrows(DuplicationException.class, () -> {
            userService.register(requestDto);
        });
    }


    @Test
    @DisplayName("요청 회원정보 가져오기")
    void getUserInfo(){
        // given
        String userId = "1111";

        // when
        User result = userService.getUserByUserId(userId);

        // then
        assertThat(result.getUserId()).isEqualTo("1111");
        assertThat(result.getEmail()).isEqualTo("test@test.test");
        assertThat(result.getPassword()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원 정보 요청 실패 가입하지 않은 회원")
    void failedGetUserInfo(){
        // given
        String userId = "9999";

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUserByUserId(userId);
        });
    }

    @Test
    @DisplayName("회원 정보 변경 성공")
    void modifyUserInfo() {
        // given
        UserRequest requestDto = UserRequest.builder()
                .userId("1111")
                .email("test@test.test")
                .name("이름")
                .password("test2")
                .build();
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        User result = userService.modify(requestDto);

        // then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getEmail()).isEqualTo("test@test.test");
        assertThat(result.getName()).isEqualTo("이름");
        assertThat(result.getPassword()).isEqualTo("test2");
        assertThat(result.getUserId()).isEqualTo("1111");
    }

    @Test
    @DisplayName("회원정보 변경 실패 로그인 회원 요청 회원 다름")
    void failedLoginWhenDifferentUser(){
        // given
        UserRequest requestDto = UserRequest.builder()
                .userId("2222")
                .email("test2@test.test")
                .name("헤헤")
                .password("test2")
                .build();
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER)
                .getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            userService.modify(requestDto);
        });
    }

    @Test
    @DisplayName("회원 정보 변경 실패 없는 회원")
    void failedLoginWhenNotFoundUser(){
        // given
        UserRequest requestDto = UserRequest.builder()
                .userId("0000")
                .email("test@test.test")
                .name("test")
                .password("test")
                .build();
        Authentication authentication = new FakeAuthentication("0000", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.modify(requestDto);
        });
    }

    @Test
    @DisplayName("회원 정보 삭제 성공")
    void successDelete(){
        // given
        String userId = "1111";
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        User result = userService.delete(userId);

        // then
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("회원 정보 삭제 성공 관리자 권한")
    void successDeleteRequestAdmin(){
        // given
        String adminId = "1111";
        Authority authority = Authority.ROLE_ADMIN;
        String userId = "3333";
        Authentication authentication = new FakeAuthentication(adminId, "test", authority).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        User result = userService.delete(userId);

        // then
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("회원 정보 삭제 실패 로그인 회원 요청 회원 다름")
    void failedDeleteWhenDifferentUser(){
        // given
        String userId = "1111";
        Authentication authentication = new FakeAuthentication("2222", "test2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            userService.delete(userId);
        });
    }

    @Test
    @DisplayName("회원 정보 삭제 실패 회원 없음")
    void failedDeleteWhenNotFoundUser(){
        // given
        String userId = "9999";
        Authentication authentication = new FakeAuthentication("9999", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.delete(userId);
        });
    }

    @Test
    @DisplayName("회원 정보 삭제 실패 이미 삭제된 회원")
    void failedDeleteWhenDeletedUser(){
        // given
        String userId = "2222";
        Authentication authentication = new FakeAuthentication("2222", "test2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            userService.delete(userId);
        });
    }

    @Test
    @DisplayName("요청 회원 목록")
    void getListByList(){
        // given
        String userId = "1111";
        String userId2 = "2222";
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(userId2);

        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Map<String, User> result = userService.getUsers(userIds);

        // then
        assertThat(result).hasSize(2)
                .containsKey("1111")
                .containsKey("2222");
    }

    @Test
    @DisplayName("이름으로 회원정보 가져오기")
    void getUserByName(){
        // given
        String name = "테스트";

        // when
        List<User> result = userService.getUserByName(name);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@test.test");
        assertThat(result.get(0).getName()).isEqualTo("테스트");
    }

}