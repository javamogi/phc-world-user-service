package com.phcworld.userservice.domain.port;

import com.phcworld.userservice.domain.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("이메일 입력값 없음")
    void emptyEmail(){
        UserRequest request = UserRequest.builder()
                .email("")
                .password("test")
                .name("test")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("이메일을 입력하세요.");
    }

    @Test
    @DisplayName("이메일 형식 아님")
    void notPatternEmail(){
        UserRequest request = UserRequest.builder()
                .email("test")
                .password("test")
                .name("test")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("이메일 형식이 아닙니다.");
    }

    @Test
    @DisplayName("비밀번호 입력 없음")
    void emptyPassword(){
        UserRequest request = UserRequest.builder()
                .email("test@test.test")
                .password("")
                .name("test")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(2);
    }

    @Test
    @DisplayName("비밀번호 글자 수 미달")
    void passwordMinSize(){
        UserRequest request = UserRequest.builder()
                .email("test@test.test")
                .password("t")
                .name("test")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);
        Iterator<ConstraintViolation<UserRequest>> iterator = constraintViolations.iterator();

        assertThat(constraintViolations).hasSize(1);
        assertThat(iterator.next().getMessage())
                .isEqualTo("비밀번호는 4자 이상으로 해야합니다.");
    }

    @Test
    @DisplayName("이름 입력값 없음")
    void emptyName(){
        UserRequest request = UserRequest.builder()
                .email("test@test.test")
                .password("test")
                .name("")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(3);
    }

    @Test
    @DisplayName("이름 형식 아님")
    void notPatternName(){
        UserRequest request = UserRequest.builder()
                .email("test@test.test")
                .password("test")
                .name("test!@#")
                .build();

        Set<ConstraintViolation<UserRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage())
                .isEqualTo("이름은 한글, 영문, 숫자만 가능합니다.");
    }
}