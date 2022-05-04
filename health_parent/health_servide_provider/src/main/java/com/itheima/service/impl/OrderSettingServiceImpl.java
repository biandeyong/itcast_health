package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    //批量导入预约设置数据
    public void add(List<OrderSetting> list) {
        if (list != null && list.size() > 0) {
            for (OrderSetting orderSetting : list) {
                //检查此数据（日期）是否存在
                long count = orderSettingDao.findCountByOrderDate(orderSetting);
                if (count > 0) {
                    //已经存在，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    //不存在，执行添加操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map> getByMouth(String date) {
        //这里数据库中的数据是2022-03-01；
        //传过来的数据是2022-3-1；
        //对ordersetting。html中的月份前面加0
        //不加0也能成功，？mybatis会自动判断？==》 sql语句 SELECT * FROM t_ordersetting WHERE orderDate BETWEEN '2022-3-4' AND '2022-3-24';也能查询成功
        //说明不需要零，加0不加0都能成功
        String begin = date + "-01";
        String end = date + "-31";
        Map<String, String> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        //调用Dao
        List<OrderSetting> orderSettingList = orderSettingDao.getByMouth(map);
        List<Map> list = new ArrayList<>();

        if (orderSettingList != null && orderSettingList.size() > 0) {
            for (OrderSetting orderSetting : orderSettingList) {
                int number = orderSetting.getNumber();
                int reservations = orderSetting.getReservations();
                Date orderDate = orderSetting.getOrderDate();
                //获取天
                int date1 = orderSetting.getOrderDate().getDate();
                //前端需要这个数据，所以map这样存储，而不是想当然
                Map<String, Integer> integerMap = new HashMap<>();
                integerMap.put("date",date1);
                integerMap.put("number",number);
                integerMap.put("reservations", reservations);
                list.add(integerMap);
            }
        }
        return list;
    }

    @Override
    public void editDate(OrderSetting orderSetting) {

        //这里的date传入后会出现问题,因为数据库的时间是UTC比中国早八个小时，所以时间传过去会变成前一天的时间
        //https://blog.csdn.net/qq631431929/article/details/51731834
        //方法，更改时区hongkong
        //还有个问题，select错误，insert能操作？？？
        long count = orderSettingDao.findCountByOrderDate(orderSetting);
        if(count > 0){
            //当前日期已经进行了预约设置，需要进行修改操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            //当前日期没有进行预约设置，进行添加操作
            orderSettingDao.add(orderSetting);
        }
    }

}
