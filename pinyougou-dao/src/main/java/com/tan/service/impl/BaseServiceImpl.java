package com.tan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.vo.BaseService;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

public   class BaseServiceImpl<T> implements BaseService<T> {

        @Autowired
        private Mapper<T> mapper;

        @Override
        public T findOne(Serializable id) {
            return mapper.selectByPrimaryKey(id);
        }

        @Override
        public List<T> findAll() {
            return mapper.selectAll();
        }

        @Override
        public List<T> findByWhere(T t) {
            return mapper.select(t);
        }

        @Override
        public PageResult findPage(Integer page, Integer rows) {
            PageHelper.startPage(page,rows);
            List<T> list = mapper.selectAll();
            PageInfo<T>  list2 = new PageInfo<>(list);
            return new PageResult(list2.getTotal(),list2.getList());
        }

        @Override
        public PageResult findPage(Integer page, Integer rows, T t) {
            PageHelper.startPage(page,rows);
            List<T> list = mapper.select(t);
        PageInfo<T>  list2 = new PageInfo<>(list);
        return new PageResult(list2.getTotal(),list2.getList());
         }

        @Override
        public void add(T t) {
            mapper.insertSelective(t);
        }

        @Override
        public void update(T t) {
            mapper.updateByPrimaryKeySelective(t);
        }

    @Override
    public void deleteByIds(Serializable[] ids) {
        for (Serializable id : ids) {
            mapper.deleteByPrimaryKey(id);
        }
    }


}
