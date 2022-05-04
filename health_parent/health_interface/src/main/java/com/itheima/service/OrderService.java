package com.itheima.service;

import com.itheima.entity.Result;
import com.itheima.pojo.Member;

import java.util.Map;

public interface OrderService {
    public Result submit(Map map) throws Exception;
    public Map findById(Integer id) throws Exception;

}
