package com.allthattour.web.user.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String firstName, lastName, email, passwd, phone;
}
