package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 会员登录
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    JedisPool jedisPool;
    @Reference
    MemberService memberService;
    @RequestMapping("/check")
    public Result check(HttpServletResponse response, @RequestBody Map map ) {
        try {
            String validateCode = (String) map.get("validateCode");
            String telephone = (String) map.get("telephone");
            String s = jedisPool.getResource().get(telephone);
            //验证码验证
            if (s == null || !s.equals(validateCode)) {
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }else {
                //验证码输入正确
                //判断当前用户是否为会员
                Member member = memberService.findByTelephone(telephone);
                if (member == null) {
                    //当前用户不是会员，自动完成注册
                    member = new Member();
                    member.setPhoneNumber(telephone);
                    member.setRegTime(new Date());
                    memberService.add(member);
                }
                //登录成功
                //写入Cookie，跟踪用户
                Cookie cookie = new Cookie("login_member_telephone", telephone);
                cookie.setPath("/");//路径
                cookie.setMaxAge(60 * 60 * 24 * 30);//有效期30天
                response.addCookie(cookie);
                //保存会员信息到Redis中
                String json = JSON.toJSON(member).toString();
                jedisPool.getResource().setex(telephone, 60 * 30, json);
                return new Result(true, MessageConstant.LOGIN_SUCCESS);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }

}