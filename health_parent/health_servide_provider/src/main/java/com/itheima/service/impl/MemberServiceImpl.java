package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.DateUtils;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService{
    @Autowired
    MemberDao memberDao;
    @Autowired
    OrderDao orderDao;
    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        //算法加密
        if(member.getPassword() != null){
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }
            memberDao.add(member);
    }

    @Override
    public Map getMemberReport() {
        Calendar calendar = Calendar.getInstance();//获得日历对象，默认为当前时间
        //计算过去十二个月
        calendar.add(Calendar.MONTH,-46);
        Map<String, Object> map = new HashMap<>();
        Date time = calendar.getTime();
        List<Integer> counts = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));
            //查询每个月的数据
            //这里使用教程给的代码会出现问题
            //在mysql8之后会验证月份合理性
            //所以要找到下一个月的第一天，从这一天开始往前查询
            //也可以找到这一个月的第一天，开始往后查询，，，不行
            try {
                String m = new SimpleDateFormat("yyyy.MM").format(calendar.getTime());
                String lastDayOfMonth = this.findlastMonth(m);
                Integer count = memberDao.findMemberCountBeforeDate(lastDayOfMonth);
                counts.add(count);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put("months", list);
        map.put("memberCount", counts);
        return map;
    }


    public String findlastMonth(String date) throws ParseException {
        String date_str = date + ".10";
        Calendar calendars = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        calendars.setTime(sdf.parse(date_str));
        calendars.add(Calendar.MONTH, 1);
        calendars.set(Calendar.DAY_OF_MONTH, 0);
        String lastDayOfMonth = sdf.format(calendars.getTime());
        //获取上一个月最后一天
        return lastDayOfMonth;
    }
}
