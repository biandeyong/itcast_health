package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public long findCountByOrderDate(OrderSetting orderSetting);

    public void editNumberByOrderDate(OrderSetting orderSetting);

    public void add(OrderSetting orderSetting);

    public List<OrderSetting> getByMouth(Map map);

    public OrderSetting findByOrderDate(Date orderDate);

    public void editReservationsByOrderDate(OrderSetting orderSetting);
}
