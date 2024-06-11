package com.phcworld.userservice.domain.port;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(
        String userId,
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일을 입력하세요.")
        String email,
        @NotBlank(message = "비밀번호를을 입력하세요.")
        @Size(min = 4, message = "비밀번호는 4자 이상으로 해야합니다.")
        String password,
        @NotBlank(message = "이름을 입력하세요.")
        @Size(min = 3, max = 20, message = "이름은 영문 3자 이상 20자 이하 또는 한글 두자이상 6자 이하로 해야합니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "이름은 한글, 영문, 숫자만 가능합니다.")
        String name,
        String imageData,
        String imageName) {
}
