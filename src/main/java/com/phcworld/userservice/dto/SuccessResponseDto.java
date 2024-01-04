package com.phcworld.userservice.dto;

import lombok.Builder;

@Builder
public record SuccessResponseDto(
        Integer statusCode,
        String message
) {
}
