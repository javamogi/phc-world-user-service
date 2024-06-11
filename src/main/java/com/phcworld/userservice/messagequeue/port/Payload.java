package com.phcworld.userservice.messagequeue.port;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class Payload {
    private String email;
    private String password;
    private String user_id;
    private String name;
    private String authority;
    private String update_date;
    private String profile_image;
    private byte is_deleted;
}
