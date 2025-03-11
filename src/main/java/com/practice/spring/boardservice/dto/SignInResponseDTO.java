package com.practice.spring.boardservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDTO {
    public boolean isLoggedIn;
    private String userId;
    private String userName;
    private String token;
}
