package com.phcworld.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginUserRequestDto(
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일을 입력하세요.")
        String email,
        @NotBlank(message = "비밀번호를을 입력하세요.")
        @Size(min = 4, message = "비밀번호는 4자 이상으로 해야합니다.")
        String password) {
}
