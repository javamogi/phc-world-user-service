package com.phcworld.userservice.medium;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
class RedisTest {

    @Autowired
    @Qualifier("redisUserRepository")
    UserRepository userRepository;

    LocalDateTime createDate = LocalDateTime.of(2024, 3, 13, 11, 11, 11, 111111);

    @BeforeEach
    void init(){
        User user = User.builder()
                .id(1L)
                .email("test@test.test")
                .password("test")
                .userId("1111")
                .name("테스트")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(createDate)
                .updateDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .id(2L)
                .email("test2@test.test")
                .password("test2")
                .userId("2222")
                .name("테스트2")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(createDate)
                .updateDate(LocalDateTime.now())
                .build();
        userRepository.save(user2);
    }

    @Test
    @DisplayName("User 도메인으로 회원 정보를 등록할 수 있다.")
    void registerUser() {
        User user3 = User.builder()
                .id(3L)
                .email("test3@test.test")
                .password("test3")
                .userId("3333")
                .name("테스트3")
                .isDeleted(false)
                .authority(Authority.ROLE_USER)
                .profileImage("blank-profile-picture.png")
                .createDate(createDate)
                .updateDate(LocalDateTime.now())
                .build();
        userRepository.save(user3);

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<User> findUser = userRepository.findByUserId("3333");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("3333");
        assertThat(findUser.get().getEmail()).isEqualTo("test3@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트3");
        assertThat(findUser.get().getCreateDate()).isEqualTo(createDate);
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(findUser.get().getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("회원 ID로 정보를 조회할 수 있다.")
    void findByUserId() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<User> findUser = userRepository.findByUserId("1111");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("1111");
        assertThat(findUser.get().getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트");
        assertThat(findUser.get().getCreateDate()).isEqualTo(createDate);
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(findUser.get().getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("등록되지 않는 회원 ID는 비어있는 Optional을 반환한다.")
    void findByEmptyUserId() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<User> findUser = userRepository.findByUserId("9999");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("회원 이름으로 회원 목록을 조회할 수 있다.")
    void findByName() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<User> findUser = userRepository.findByName("테스트");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).hasSize(1);
        assertThat(findUser.get(0).getUserId()).isEqualTo("1111");
        assertThat(findUser.get(0).getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get(0).getName()).isEqualTo("테스트");
        assertThat(findUser.get(0).getCreateDate()).isEqualTo(createDate);
        assertThat(findUser.get(0).getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(findUser.get(0).getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("등록되지 않은 회원 이름으로 회원 목록을 조회하면 비어있는 List를 반환한다.")
    void findByNameWhenNotFound() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<User> findUser = userRepository.findByName("등록되지않는이름");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("회원 이메일로 정보를 조회할 수 있다.")
    void findByEmail() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<User> findUser = userRepository.findByEmail("test@test.test");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("1111");
        assertThat(findUser.get().getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트");
        assertThat(findUser.get().getCreateDate()).isEqualTo(createDate);
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
        assertThat(findUser.get().getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("등록되지 않은 회원 이메일로 정보를 조회할 수 없다.")
    void findByEmailWhenNotFound() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<User> findUser = userRepository.findByEmail("9999@test.test");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("회원 ID List로 정보 목록을 조회할 수 있다.")
    void findByUserIds() {
        List<String> list = List.of("1111","2222");
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<User> findUser = userRepository.findByUserIds(list);
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).hasSize(2);
    }

    @Test
    @DisplayName("등록되지 않은 회원 ID List로 정보 목록을 조회하면 비어있는 List를 반환한다.")
    void findByUserIdsWhenNotFound() {
        List<String> list = List.of("0000","9999");
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<User> findUser = userRepository.findByUserIds(list);
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isEmpty();
    }

}
