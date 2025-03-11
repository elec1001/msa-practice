package com.practice.spring.boardservice.controller;

import com.practice.spring.boardservice.config.jwt.TokenProvider;
import com.practice.spring.boardservice.config.security.CustomUserDetails;
import com.practice.spring.boardservice.dto.*;
import com.practice.spring.boardservice.model.Member;
import com.practice.spring.boardservice.service.MemberService;
import com.practice.spring.boardservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;//@Bean 등록 필요
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("join")
    public SignUpResponseDTO signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {

        System.out.println("signUp!! :: "+signUpRequestDTO);

        memberService.signUp(signUpRequestDTO.toMember(bCryptPasswordEncoder));

        return SignUpResponseDTO.builder()
                .successed(true)
                .build();
    }

    @PostMapping("/login")
    public SignInResponseDTO signIn(@RequestBody SignInRequestDTO signInRequestDTO,
                                    HttpServletResponse response) {
        System.out.println("signIn!! :: "+signInRequestDTO);
        //UsernamePasswordAuthenticationToken객체를 만들어 인증시도하여 성공하면 Authentication객체 리턴
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequestDTO.getUsername(),
                        signInRequestDTO.getPassword()
                )
        );
        //컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //인증된 사용자 정보를 멤버에 저장
        Member member=((CustomUserDetails)authentication.getPrincipal()).getMember();

        //토큰 발생
        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2));
        String refreshToken = tokenProvider.generateToken(member,Duration.ofDays(2));

        return SignInResponseDTO.builder()
                .isLoggedIn(true)
                .token(accessToken)
                .userId(member.getUserId())
                .userName(member.getUserName())
                .build();
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request,HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,"refreshToken");
    }


    // 클라이언트가 GET /user/info 요청을 보내면, request에서 Member 객체를 가져와 사용자 정보를 DTO로 변환 후 반환합니다.
   //request.getAttribute("member")는 필터나 인터셉터에서 설정된 값일 가능성이 큽니다.
   //UserInfoResponseDTO를 사용하여 보안과 데이터 전송의 효율성을 높입니다.

    @GetMapping("/user/info")
    public UserInfoResponseDTO getUserInfo(HttpServletRequest request) {
        //getAttribute의 반환값이 Object 타입이므로 Member 타입으로 캐스팅
        Member member=(Member) request.getAttribute("member");

        return UserInfoResponseDTO.builder()
                .id(member.getId())
                .userName(member.getUserName())
                .userId(member.getUserId())
                .role(member.getRole())
                .build();
    }


}
