package com.itheima.controller;


import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.TencentSMS;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        try {
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            TencentSMS.TencentSMS(telephone,s);
            //将验证码存入redis

            jedisPool.getResource().setex(telephone,5*60,s);
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

    }@RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        try {
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            TencentSMS.TencentSMS(telephone,s);
            //将验证码存入redis

            jedisPool.getResource().setex(telephone,5*60,s);
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

    }

}
