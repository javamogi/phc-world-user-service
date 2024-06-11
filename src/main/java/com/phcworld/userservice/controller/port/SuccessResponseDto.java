package com.phcworld.userservice.controller.port;

import lombok.Builder;

@Builder
public record SuccessResponseDto(
        Integer statusCode,
        String message
) {
}
