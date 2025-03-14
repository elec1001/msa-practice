package com.practice.spring.boardservice.controller;

import com.practice.spring.boardservice.config.jwt.TokenProvider;
import com.practice.spring.boardservice.dto.SignInResponseDTO;
import com.practice.spring.boardservice.model.Member;
import com.practice.spring.boardservice.service.TokenService;
import com.practice.spring.boardservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class TokenApiController {
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String newAccessToken = tokenService.refreshToken(request, response);

        return ResponseEntity.ok(
                SignInResponseDTO.builder()
                        .token(newAccessToken)
                        .build()
        );
    }
}
