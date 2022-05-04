package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.lang.ref.Reference;
import java.util.List;

public interface CheckGroupService {
    public void add(CheckGroup checkGroup, Integer[] checkitemIds);
    public PageResult findPage(QueryPageBean queryPageBean);
    public CheckGroup findById(Integer id);
    public Integer[] findIds(Integer id);
    public void editGroup(CheckGroup checkGroup);
    public void deleteAssociation(Integer id);
    public void editGI(Integer id,Integer[] ids);
    public List<CheckGroup> findAll();
}
