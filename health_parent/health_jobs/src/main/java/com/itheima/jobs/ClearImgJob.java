package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 定时清理
 */
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(sdiff!=null){
            for (String s : sdiff) {
                //删除图片
                QiniuUtils.deleteFileFromQiniu(s);
                System.out.println("已删除"+s);
                //删除redis中的图片
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, s);
            }
        }else {
            System.out.println("nimasjhil ");
        }
    }
}
