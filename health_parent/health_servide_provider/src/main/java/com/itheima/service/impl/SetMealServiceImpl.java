package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.OrderDao;
import com.itheima.dao.SetMealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetMealService.class)
@Transactional
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private SetMealDao setMealDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${out_put_path}")
    private String outputPath;

    @Override
    public void addsetmeal(Setmeal setmeal, Integer[] ids) {
        setMealDao.addsetmeal(setmeal);
        //这里在service能取到id，在controller不能取到
        //在service调用dao，调用接口，返回的id会直接赋值到setmeal上，但是controller的setmeal跟service不是一个，所以不能赋值到controller
        //在使用addsetmeal方法后不能马上写入数据库？？？
        //为什么现在又你能写入数据库了《《《《《
        //okok，可以的
        //取到id后不能在同一个方法中去addSC，因为在使用addsetmeal方法后，不能立即写入数据库，数据库中无信息
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());
        Integer id = setmeal.getId();
        for (Integer cid : ids) {
            Map<String, Integer> map = new HashMap<>();
            map.put("setmeal_id", id);
            map.put("checkgroup_id", cid);
            setMealDao.addSC(map);
        }
        //将图片的信息保存到redis
        //(生成静态页面) 套餐列表页面，套餐详情页面
        //静态网页并不会覆盖原有静态网页
        //解决办法，将原有的文件删除，new File(outputPath + "\\" + htmlname).delete();
        generateMobileStaticHtml();

    }

    //生成静态页面
    public void generateMobileStaticHtml() {
        //准备模板文件中所需的数据
        List<Setmeal> setmealList = this.find();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面（多个）
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("setmealList", setmealList);
        this.generateHtml("mobile_setmeal.ftl", "m_setmeal.html", dataMap);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("setmeal", this.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_" + setmeal.getId() + ".html",
                    dataMap);
        }
    }

    //生成静态页面
    public void generateHtml(String name, String htmlname, Map map) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        BufferedWriter out = null;
        try {
            Template template = configuration.getTemplate(name);
            new File(outputPath + "\\" + htmlname).delete();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath + "\\" + htmlname, true), "UTF-8"));
            template.process(map, out);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override

    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        String queryString = queryPageBean.getQueryString();
        Integer pageSize = queryPageBean.getPageSize();
        if (queryString == null) {
            currentPage = 1;
        }
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setMealDao.findPage(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Setmeal> find() {
        return setMealDao.find();
    }

    @Override
    public Setmeal findById(Integer integer) {
        return setMealDao.findById(integer);
    }

    @Override
    public Map getSetmealReport() {
        //出现数组空指针异常，解决办法先查数量再赋值
        int counts = setMealDao.findcounts();
        String[] setmealNames = new String[counts];
        Map<String, Object> map2 = new HashMap<>();
        List<Map> list = new ArrayList<>();
        List<Setmeal> setmeals = setMealDao.find();
        int i = 0;
        for (Setmeal setmeal : setmeals) {
            Map<String, Object> map = new HashMap<>();
            String name = setmeal.getName();
            setmealNames[i] = name;
            i++;
            int count = orderDao.findBySetmealId(setmeal.getId());
            map.put("value", count);
            map.put("name", name);
            //这里list的值会覆盖原来的值？？？？？
            //https://blog.csdn.net/ChaoticNg/article/details/121669316
            //在for循环里面Map map = new HashMap(); 即可，每次在堆中创建一个新的map，防止覆盖。
            //因为list指向的是map 的地址，所以不管什么时候查list，在map中就只有一个数据
            list.add(map);
        }
        map2.put("setmealName", setmealNames);
        map2.put("setmealCount", list);
        return map2;
    }
}
