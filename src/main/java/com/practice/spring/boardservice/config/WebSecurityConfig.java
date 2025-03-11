package com.practice.spring.boardservice.config;

import com.practice.spring.boardservice.config.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        //필터를 거치지 않게 예외처리
        return (web->web.ignoring()
                .requestMatchers(
                        "/static/**",
                        "/css/**",
                        "/js/**"
                ));
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter)throws  Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sesion->sesion
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers(
                                        //화면이동
                                        new AntPathRequestMatcher("/", GET.name()),
                                        new AntPathRequestMatcher("/member/join",GET.name()),
                                        new AntPathRequestMatcher("/member/login",GET.name()),
                                        new AntPathRequestMatcher("/write",GET.name()),
                                        new AntPathRequestMatcher("/access-denied",GET.name()),
                                        new AntPathRequestMatcher("/detail",GET.name()),
                                        //기능
                                        new AntPathRequestMatcher("/refresh-token",POST.name()),
                                        new AntPathRequestMatcher("/join",POST.name()),
                                        new AntPathRequestMatcher("/login",POST.name()),
                                        new AntPathRequestMatcher("logout", POST.name()),
                                        new AntPathRequestMatcher("/api/board/file/download/*", GET.name())
                                )
                                .permitAll()
                                .anyRequest().authenticated()//퍼밋올을 제외한 모든 요청에 인증을 요구

                )
                .logout(AbstractHttpConfigurer::disable)
                //jwt 필터 추가
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception->exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
        ;
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        new BCryptPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/access-denied");
        };
    }

}
