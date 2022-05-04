package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.Map;

public interface MemberService {
    public Member findByTelephone(String telephone);
    public void add(Member member);
    public Map getMemberReport();
}
