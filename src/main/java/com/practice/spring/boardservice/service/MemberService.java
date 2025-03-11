package com.practice.spring.boardservice.service;

import com.practice.spring.boardservice.mapper.MemberMapper;
import com.practice.spring.boardservice.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public void signUp(Member member){
        memberMapper.saved(member);
    }
}
