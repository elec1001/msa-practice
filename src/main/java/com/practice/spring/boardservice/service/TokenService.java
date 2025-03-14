package com.practice.spring.boardservice.service;

import com.practice.spring.boardservice.config.jwt.TokenProvider;
import com.practice.spring.boardservice.dto.SignInResponseDTO;
import com.practice.spring.boardservice.exception.UnauthorizedException;
import com.practice.spring.boardservice.model.Member;
import com.practice.spring.boardservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;

    public String refreshToken(HttpServletRequest request,
                               HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request,"refresh_token");

        if (refreshToken != null && tokenProvider.validToken(refreshToken) == 1) {
            Member member = tokenProvider.getTokenDetails(refreshToken);

            String newAccessToken  = tokenProvider.generateToken(member, Duration.ofHours(2));
            String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(2));

            CookieUtil.addCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60);

            response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

            return newAccessToken;
        } else {
           throw new UnauthorizedException("Refresh Token이 유효하지 않습니다.");
        }
    }

}
