package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealService {

    public void addsetmeal(Setmeal setmeal, Integer[] ids);

    public PageResult findPage(QueryPageBean queryPageBean);

    public List<Setmeal> find();

    public Setmeal findById(Integer integer);
    public Map getSetmealReport();

}
