package com.phcworld.userservice.domain.port;

import com.phcworld.userservice.domain.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("이메일 입력값 없음")
    void emptyEmail(){
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("test")
                .build();

        Set<ConstraintViolation<LoginRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("이메일을 입력하세요.");
    }

    @Test
    @DisplayName("이메일 형식 아님")
    void notPatternEmail(){
        LoginRequest request = LoginRequest.builder()
                .email("test")
                .password("test")
                .build();

        Set<ConstraintViolation<LoginRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("이메일 형식이 아닙니다.");
    }

    @Test
    @DisplayName("비밀번호 입력 없음")
    void emptyPassword(){
        LoginRequest request = LoginRequest.builder()
                .email("test@test.test")
                .password("")
                .build();

        Set<ConstraintViolation<LoginRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(2);
    }

    @Test
    @DisplayName("비밀번호 글자 수 미달")
    void passwordMinSize(){
        LoginRequest request = LoginRequest.builder()
                .email("test@test.test")
                .password("t")
                .build();

        Set<ConstraintViolation<LoginRequest>> constraintViolations =
                validator.validate(request);
        Iterator<ConstraintViolation<LoginRequest>> iterator = constraintViolations.iterator();

        assertThat(constraintViolations).hasSize(1);
        assertThat(iterator.next().getMessage())
                .isEqualTo("비밀번호는 4자 이상으로 해야합니다.");
    }

}