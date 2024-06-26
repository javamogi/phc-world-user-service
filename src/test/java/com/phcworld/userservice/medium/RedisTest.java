package com.phcworld.userservice.medium;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.infrastructure.UserRedisEntity;
import com.phcworld.userservice.infrastructure.UserRedisRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
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
    UserRedisRepositoryImpl userRedisRepository;

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
        userRedisRepository.save(user);

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
        userRedisRepository.save(user2);
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
        userRedisRepository.save(user3);

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<UserRedisEntity> findUser = userRedisRepository.findByUserId("3333");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("3333");
        assertThat(findUser.get().getEmail()).isEqualTo("test3@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트3");
        assertThat(findUser.get().getCreateDate())
                .isEqualTo(createDate.withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
    }

    @Test
    @DisplayName("회원 ID로 정보를 조회할 수 있다.")
    void findByUserId() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<UserRedisEntity> findUser = userRedisRepository.findByUserId("1111");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("1111");
        assertThat(findUser.get().getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트");
        assertThat(findUser.get().getCreateDate())
                .isEqualTo(createDate.withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
    }

    @Test
    @DisplayName("회원 이름으로 회원 목록을 조회할 수 있다.")
    void findByName() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<UserRedisEntity> findUser = userRedisRepository.findByName("테스트");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).hasSize(1);
        assertThat(findUser.get(0).getUserId()).isEqualTo("1111");
        assertThat(findUser.get(0).getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get(0).getName()).isEqualTo("테스트");
        assertThat(findUser.get(0).getCreateDate())
                .isEqualTo(createDate.withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        assertThat(findUser.get(0).getProfileImage()).isEqualTo("blank-profile-picture.png");
    }

    @Test
    @DisplayName("회원 이메일로 정보를 조회할 수 있다.")
    void findByEmail() {
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<UserRedisEntity> findUser = userRedisRepository.findByEmail("test@test.test");
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUserId()).isEqualTo("1111");
        assertThat(findUser.get().getEmail()).isEqualTo("test@test.test");
        assertThat(findUser.get().getName()).isEqualTo("테스트");
        assertThat(findUser.get().getCreateDate())
                .isEqualTo(createDate.withNano(0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        assertThat(findUser.get().getProfileImage()).isEqualTo("blank-profile-picture.png");
    }

    @Test
    @DisplayName("회원 ID List로 정보 목록을 조회할 수 있다.")
    void findByUserIds() {
        List<String> list = List.of("1111","2222");
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<UserRedisEntity> findUser = userRedisRepository.findByUserIds(list);
        queryStopWatch.stop();
        log.info("findByUserId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());

        assertThat(findUser).hasSize(2);
    }

}
