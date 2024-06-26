package com.phcworld.userservice.messagequeue.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KafkaUserDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
