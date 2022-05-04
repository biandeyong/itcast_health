package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealDao {

    public void addSC(Map<String, Integer> map);
    public void addsetmeal(Setmeal setmeal);
    public Page<Setmeal> findPage(String queryString);
    public List<Setmeal> find();
    public Setmeal findById(Integer id);
    public Integer findcounts();
}

