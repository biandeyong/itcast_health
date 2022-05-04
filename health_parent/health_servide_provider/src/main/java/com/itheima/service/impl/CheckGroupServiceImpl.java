package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查组服务
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
//事务注解
public class CheckGroupServiceImpl  implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;

    //新增检查组，同时检查组关联检查项
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //操作检查组,获取检查组的id
        checkGroupDao.add(checkGroup);
        Integer id = checkGroup.getId();
        //操作检查项关联检查项，多对多
        if(checkitemIds!=null&&checkitemIds.length>0){
            for (Integer cid: checkitemIds) {
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroupId",id);
                map.put("checkitemId", cid);
                checkGroupDao.set(map);
            }
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer pageSize = queryPageBean.getPageSize();
        Integer currentPage = queryPageBean.getCurrentPage();
        String queryString = queryPageBean.getQueryString();
        if(queryString!=null){
            currentPage=1;
        }
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return  checkGroupDao.findById(id);
    }

    @Override
    public Integer[] findIds(Integer id) {
      return   checkGroupDao.findIds(id);
    }

    @Override
    public void editGroup(CheckGroup checkGroup) {
        checkGroupDao.editGroup(checkGroup);
    }

    @Override
    public void deleteAssociation(Integer id) {
        checkGroupDao.deleteAssociation(id);
    }

    @Override
    public void editGI(Integer id, Integer[] ids) {

        if(ids != null && ids.length > 0){
            for (Integer checkitemId : ids) {
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroup_id",id);
                map.put("checkitem_id",checkitemId);
                checkGroupDao.editGI(map);
            }
        }
    }

    @Override
    public List<CheckGroup> findAll() {
       return checkGroupDao.findAll();
    }
}
