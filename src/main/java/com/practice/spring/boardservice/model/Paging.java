package com.practice.spring.boardservice.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Paging {
    public int offset;
    public int size;
}
