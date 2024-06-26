package com.phcworld.userservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.InternalServerErrorException;
import com.phcworld.userservice.messagequeue.dto.*;
import com.phcworld.userservice.service.port.UserProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProducerImpl implements UserProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("string", false, "email"),
            new Field("string", false, "password"),
            new Field("string", false, "user_id"),
            new Field("string", true, "name"),
            new Field("string", false, "authority"),
            new Field("string", true, "profile_image"),
            new Field("int8", false, "is_deleted"),
            new Field("string", false, "update_date")
//            new Field("int64", true, "create_date")
//            {
//                public String name="org.apache.kafka.connect.data.Timestamp";
//                public int version = 1;
//            }
            );
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("users")
            .build();

    @Override
    public User send(String topic, User user){
        log.info("user : {}", user);
        Payload payload = Payload.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .user_id(user.getUserId())
                .name(user.getName())
                .authority(user.getAuthority().toString())
                .update_date(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .profile_image(user.getProfileImage())
                .is_deleted((byte) (Boolean.TRUE.equals(user.isDeleted()) ? 1 : 0))
                .build();

        KafkaUserDto kafkaUserDto = KafkaUserDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaUserDto);
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, user.getEmail(), jsonInString);

        kafkaTemplate.send(record);
//        kafkaTemplate.send(topic, jsonInString);
//        log.info("User Producer sent data from the User microservice: {}", kafkaUserDto);
        log.info("User Producer sent data from the User microservice: {}", record);

        return user;
    }
}
