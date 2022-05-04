package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约设置
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {


    @Reference
    private OrderSettingService orderSettingService;

    //文件上传
    @RequestMapping("/upload")
    //文件上传使用MultipartFile
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile){
        //使用工具类解析
        try {
            List<String[]> strings = POIUtils.readExcel(excelFile);
            //通过list来装载数据
//            ordersetting来获取数据，然后放入list中，再调用service
            List<OrderSetting> data = new ArrayList<>();
            for (String[] string : strings) {
                String orderdata = string[0];
                String number = string[1];
                //预约时间，可预约人数
                Date date = new Date(orderdata);

                OrderSetting orderSetting = new OrderSetting(date, Integer.parseInt(number));
                data.add(orderSetting);
            }
            //通过dubbo远程调用服务，批量导入
            orderSettingService.add(data);
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);

        } catch (IOException e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }

    }
    //根据月份查询数据
    @RequestMapping("/getByMouth")

    public Result getByMouth( String date){//yyyy-MM,这里是放在url，不用解析
        try {
            List<Map>  list = orderSettingService.getByMouth(date);
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,list);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);

        }
    }

    @RequestMapping("/editDate")
    public Result editDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.editDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);

        }
    }
}
