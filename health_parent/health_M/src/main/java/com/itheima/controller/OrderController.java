package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.TencentSMS;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    OrderService orderService;
    @Autowired
    private JedisPool jedisPool;
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map ){
            //短信校验
            String telephone = (String) map.get("telephone");
            //从Redis中获取缓存的验证码，key为手机号+RedisConstant.SENDTYPE_ORDER
            String codeInRedis = jedisPool.getResource().get(telephone);
            String validateCode = (String) map.get("validateCode");
            //校验手机验证码
            if(codeInRedis == null || !codeInRedis.equals(validateCode)){
                return new Result(false, MessageConstant.VALIDATECODE_ERROR);
            }
            Result  submit =null;
            try {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            submit = orderService.submit(map);
            return submit;
        } catch (Exception e) {
            e.printStackTrace();
            return submit;
        }

    }
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
          Map map =   orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }


    }
}
