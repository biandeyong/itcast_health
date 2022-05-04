package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    public void add(CheckGroup checkGroup);
    public void set(Map map);
    public Page<CheckGroup> findByCondition(String queryString);
    public CheckGroup findById(Integer id);
    public Integer[] findIds(Integer id);
    public void editGroup(CheckGroup checkGroup);
    public void editGI(Map map);
    public  void deleteAssociation(Integer id);
    public List<CheckGroup> findAll();
}
