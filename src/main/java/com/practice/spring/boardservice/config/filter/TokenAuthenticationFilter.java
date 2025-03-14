package com.practice.spring.boardservice.config.filter;

import com.practice.spring.boardservice.config.jwt.TokenProvider;
import com.practice.spring.boardservice.model.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI=request.getRequestURI();
        log.info("requestURI:{}",requestURI);
        if("/refresh-token".equals(requestURI)){
            filterChain.doFilter(request,response);
            return;
        }

        String token = resolveToken(request);
        if (token != null && tokenProvider.validToken(token)==1) {
            //토큰이 유효할 경우 ,인증 정보를 설정
            Authentication authentication=tokenProvider.getAuthentcation(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Member member=tokenProvider.getTokenDetails(token);
            request.setAttribute("member", member);
        } else if (token !=null && tokenProvider.validToken(token)==2) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 더 이상 진행하지 않음.
        }
        filterChain.doFilter(request, response);
    }

    //http요청 헤더에서 토큰을 추출,bearer는 소지자란 뜻으로 토큰기반 인증의 표준을 나타냄
    //GET /api/protected-resource HTTP/1.1
    //Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
