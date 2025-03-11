package com.practice.spring.boardservice.model;

import com.practice.spring.boardservice.type.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private Role role;

}
