package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    public void add(List<OrderSetting> list);
    public List<Map>  getByMouth(String date);
    public void editDate(OrderSetting orderSetting);

}
