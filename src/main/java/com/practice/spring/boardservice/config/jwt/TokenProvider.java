package com.practice.spring.boardservice.config.jwt;

import com.practice.spring.boardservice.model.Member;
import com.practice.spring.boardservice.type.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private final JWTProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return makeToken(
                member,
                new Date(now.getTime()+expiredAt.toMillis())
        );
    }

    private String makeToken(Member member, Date expired) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expired)
                .setSubject(member.getUserId())
                .claim("id",member.getId())
                .claim("role",member.getRole())
                .claim("userName",member.getUserName())
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public int validToken(String token) {
        try {
            getClaims(token);
            return 1;
        } catch (ExpiredJwtException e){
            //토큰이 만료된 경우
            log.info("token expired");
            return 2;
        } catch (Exception e){
            //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            System.out.println("token 복호화 에러 : "+e.getMessage());
            return 3;
        }
}
    // JWT에서 클레임 정보를 안전하게 가져오기 위한 기능
    //JWT가 변조되지 않았는지 검증한 후, 토큰에 포함된 정보를 추출하는 역할
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()  // JWT 파서(Parser) 생성
                .setSigningKey(getSecretKey())  // 서명 검증을 위한 키 설정
                .build()  // JWT 파서 객체 생성
                .parseClaimsJws(token)  // JWT를 파싱하고 서명을 검증
                .getBody();  // JWT의 Body 부분(Claims)을 가져옴
    }

    public Authentication getAuthentcation(String token) {
        Claims claims = getClaims(token);

        //Claims에서 역활을 추출하고,GrantedAuthority로 변환
        List<GrantedAuthority> authorities= Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("role",String.class))
        );

        //UserDetails 객체 생성
        UserDetails userDetails=new User(claims.getSubject(),"",authorities);
        
        //UsernamePasswordAuthenticationToken 셍성
        return new UsernamePasswordAuthenticationToken(userDetails,token,authorities);
    }

    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token);

        return Member.builder()
                .id(claims.get("id",Long.class))
                .userId(claims.getSubject())
                .userName(claims.get("userName",String.class))
                .role(
                        Role.valueOf(claims.get("role",String.class))
                )
                .build();
    }
}
