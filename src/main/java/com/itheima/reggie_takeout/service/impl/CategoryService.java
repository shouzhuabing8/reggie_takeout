package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
