package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.container.page.PageHandler;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务
 */
//明确实现的是哪个服务接口
@Service(interfaceClass = CheckItemService.class)
@Transactional//事务注解
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
   private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage =  queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        if(queryString!=null){
            currentPage=1;
        }
        //使用mybatis 的分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> checkItems = checkItemDao.selectByCondition(queryString);
        Long total = checkItems.getTotal();
        List<CheckItem> rows = checkItems.getResult();
        return  new PageResult(total,rows);
    }

    @Override
    public void deleteById(Integer id) {
        //判断是否关联到检查组
        Long countCheckItemId =  checkItemDao.findCountByCheckItemId(id);
        if(countCheckItemId>0){
            //不允许删除
            throw new RuntimeException("当前检查项被引用，不能删除");
        }
        checkItemDao.deleteById(id);

    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
       return checkItemDao.findById(id);
    }

    @Override
    public List<CheckItem> findAll() {
      return   checkItemDao.findAll();

    }
}
