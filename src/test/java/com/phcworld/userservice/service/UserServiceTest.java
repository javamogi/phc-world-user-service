package com.phcworld.userservice.service;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.dto.LoginUserRequestDto;
import com.phcworld.userservice.dto.SuccessResponseDto;
import com.phcworld.userservice.dto.UserRequestDto;
import com.phcworld.userservice.dto.UserResponseDto;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.DuplicationException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.dto.TokenDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserService userService;

    @Test
    void 회원가입() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("test3@test.test")
                .password("test3")
                .name("테스트3")
                .build();

        User user = User.builder()
                .email(requestDto.email())
                .password(requestDto.password())
                .name(requestDto.name())
                .profileImage("blank-profile-picture.png")
                .authority(Authority.ROLE_USER)
                .userId(UUID.randomUUID().toString())
                .createDate(LocalDateTime.now())
                .build();

        when(userService.registerUser(requestDto)).thenReturn(user);
        User savedUser = userService.registerUser(requestDto);
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void 회원가입_실패_가입된_이메일(){
        UserRequestDto requestDto = UserRequestDto.builder()
                .email("test@test.test")
                .password("test")
                .name("test")
                .build();
        when(userService.registerUser(requestDto)).thenThrow(DuplicationException.class);
        Assertions.assertThrows(DuplicationException.class, () -> {
            userService.registerUser(requestDto);
        });
    }

    @Test
    void 회원_정보_가져오기(){
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email("test@test.test")
                .name("테스트")
                .createDate("방금전")
                .build();
        String userId = UUID.randomUUID().toString();
        when(userService.getUserInfo(userId)).thenReturn(userResponseDto);
        UserResponseDto result = userService.getUserInfo(userId);
        assertThat(result).isEqualTo(userResponseDto);
    }

    @Test
    void 회원_정보_요청_없는_회원(){
        String userId = UUID.randomUUID().toString();
        when(userService.getUserInfo(userId)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUserInfo(userId);
        });
    }

    @Test
    void 회원_정보_변경_성공() throws IOException {
        File file = new File("src/main/resources/static/image/PHC-WORLD.png");
        byte[] bytesFile = Files.readAllBytes(file.toPath());
        String imgData = Base64.getEncoder().encodeToString(bytesFile);

        String userId = UUID.randomUUID().toString();
        UserRequestDto requestDto = UserRequestDto.builder()
                .userId(userId)
                .email("test@test.test")
                .name("test")
                .password("test")
                .imageData(imgData)
                .imageName("test.png")
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email("test@test.test")
                .name("test")
                .createDate("방금전")
                .profileImage("imgUrl")
                .userId(userId)
                .build();
        when(userService.modifyUserInfo(requestDto)).thenReturn(userResponseDto);
        UserResponseDto result = userService.modifyUserInfo(requestDto);
        assertThat(result).isEqualTo(userResponseDto);
    }

    @Test
    void 회원_정보_변경_실패_로그인_회원_요청_회원_다름(){
        String userId = UUID.randomUUID().toString();
        UserRequestDto requestDto = UserRequestDto.builder()
                .userId(userId)
                .email("test@test.test")
                .name("test")
                .password("test")
                .build();

        when(userService.modifyUserInfo(requestDto)).thenThrow(UnauthorizedException.class);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.modifyUserInfo(requestDto);
        });
    }

    @Test
    void 회원_정보_변경_실패_없는_회원(){
        String userId = UUID.randomUUID().toString();
        UserRequestDto requestDto = UserRequestDto.builder()
                .userId(userId)
                .email("test@test.test")
                .name("test")
                .password("test")
                .build();

        when(userService.modifyUserInfo(requestDto)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.modifyUserInfo(requestDto);
        });
    }

    @Test
    void 회원_정보_삭제_성공(){
        String userId = UUID.randomUUID().toString();
        SuccessResponseDto successResponseDto = SuccessResponseDto.builder()
                .statusCode(200)
                .message("삭제 성공")
                .build();

        when(userService.deleteUser(userId)).thenReturn(successResponseDto);
        SuccessResponseDto result = userService.deleteUser(userId);
        assertThat(result).isEqualTo(successResponseDto);
    }

    @Test
    void 회원_정보_삭제_실패_로그인_회원_요청_회원_다름(){
        String userId = UUID.randomUUID().toString();
        when(userService.deleteUser(userId)).thenThrow(UnauthorizedException.class);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    @Test
    void 회원_정보_삭제_실패_회원_없음(){
        String userId = UUID.randomUUID().toString();
        when(userService.deleteUser(userId)).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    @Test
    void 회원_아이디_리스트_회원_정보_불러오기(){
        List<String> userIds = new ArrayList<>();
        Map<String, UserResponseDto> list = userService.getUsersByUserIdList(userIds);

        String userId = UUID.randomUUID().toString();
        UserResponseDto userResponseDto1 = UserResponseDto.builder()
                .email("test@test.test")
                .name("테스트")
                .createDate("방금전")
                .userId(userId)
                .build();
        userIds.add(userId);

        String userId2 = UUID.randomUUID().toString();
        UserResponseDto userResponseDto2 = UserResponseDto.builder()
                .email("test2@test.test")
                .name("테스트2")
                .createDate("방금전")
                .userId(userId2)
                .build();
        userIds.add(userId2);

        list.put(userId, userResponseDto1);
        list.put(userId2, userResponseDto2);

        when(userService.getUsersByUserIdList(userIds)).thenReturn(list);
        Map<String, UserResponseDto> result = userService.getUsersByUserIdList(userIds);
        assertThat(result)
                .hasSize(2)
                .contains(entry(userId, userResponseDto1))
                .contains(entry(userId2, userResponseDto2));
    }

}