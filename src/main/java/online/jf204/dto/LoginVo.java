package com.example.demo2.dto;

import com.example.demo2.entity.User;
import lombok.Data;

@Data
public class LoginVo {
    private Integer id;
    private String token;
    private User user;

}
