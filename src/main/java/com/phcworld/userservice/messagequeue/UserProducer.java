package com.phcworld.userservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.dto.Field;
import com.phcworld.userservice.dto.KafkaUserDto;
import com.phcworld.userservice.dto.Payload;
import com.phcworld.userservice.dto.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("string", false, "email"),
            new Field("string", false, "password"),
            new Field("string", false, "user_id"),
            new Field("string", true, "name"),
            new Field("string", true, "authority"),
            new Field("string", true, "profile_image"),
            new Field("int8", true, "is_deleted"),
            new Field("int64", true, "create_date")
            {
                public String name="org.apache.kafka.connect.data.Timestamp";
                public int version = 1;
            });
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("users")
            .build();

    public User send(String topic, User user){
        ZoneId zoneid = ZoneId.of("Asia/Seoul");
        Payload payload = Payload.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .user_id(user.getUserId())
                .name(user.getName())
                .authority(user.getAuthority().toString())
                .create_date(user.getCreateDate().atZone(zoneid).toInstant().toEpochMilli())
                .profile_image(user.getProfileImage())
                .is_deleted(0)
                .build();

        KafkaUserDto kafkaUserDto = KafkaUserDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaUserDto);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("User Producer sent data from the User microservice: {}", kafkaUserDto);

        return user;
    }
}
