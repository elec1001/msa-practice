package com.practice.spring.boardservice.mapper;

import com.practice.spring.boardservice.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void saved(Member member);

    Member findByUserId(String username);
}
