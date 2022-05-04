package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.pojo.Setmeal;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderSettingDao orderSettingDao;
    @Autowired
    MemberDao memberDao;
    @Autowired
    OrderDao orderDao;

    @Override
    public Result submit(Map map) throws Exception {
        //1.根据日期查看是否能预约
        //2.查看预约人数是否已满
        //3.是否重复预约
        //4.是否为会员
        String orderDate1 = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate1);
        OrderSetting orderDate = orderSettingDao.findByOrderDate(date);
       if(orderDate==null){
           return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
       }
        if (orderDate.getReservations() >= orderDate.getNumber()) {
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //检查当前用户是否为会员，根据手机号判断
        Member member = memberDao.findByTelephone((String) map.get("telephone"));
        //防止重复预约
        if(member != null){
            Integer memberId = member.getId();
            int setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId,date,null,null,setmealId);
            List<Order> list = orderDao.findByCondition(order);
            if(list != null && list.size() > 0){
                //已经完成了预约，不能重复预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }
        //可以预约，设置预约人数加一
        orderDate.setReservations(orderDate.getReservations()+1);

        orderSettingDao.editReservationsByOrderDate(orderDate);
        if(member == null){
            //当前用户不是会员，需要添加到会员表
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber((String) map.get("telephone"));
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);
        }

        //保存预约信息到预约表
        Order order = new Order(member.getId(),
                date,
                (String)map.get("orderType"),
                Order.ORDERSTATUS_NO,
                Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
            //1根据id查order
        //根据orderid查memberid查membername
        //根据orderid查setmealid然后查setmeal

        //这里使用多表联查，，，，，member是关键字，做别名要加双引号
        //***************************************GGGGG
        Map map = orderDao.findById(id);
        if(map != null){
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}
