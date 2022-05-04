package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/**
 * 体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    //使用JedisPool操作redis
    @Autowired
    private JedisPool jedisPooll;

    @Reference
    private SetMealService setMealService;

    @RequestMapping("/upload")
    //将请求参数绑定到你控制器的方法参数上（是springmvc中接收普通参数的注解）
    //关于RequestBody 接收的是请求体里面的数据；而RequestParam接收的是key-value
    //一般传回json用RequestBody
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        String originalFilename = imgFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(i);
        //生产随机的文件名
        String fileName = UUID.randomUUID().toString()+substring;
        try{
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            jedisPooll.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);
        }catch(IOException e){
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }
    @RequestMapping("/add")
    //checkgroupIds名称要跟前端穿回来的一样
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
    try{
        //新增
        setMealService.addsetmeal(setmeal,checkgroupIds);
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS );
    }catch(Exception e){
        e.printStackTrace();
        return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
    }
}
        @RequestMapping("/findPage")
        public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
              return  setMealService.findPage(queryPageBean);
        }
}
