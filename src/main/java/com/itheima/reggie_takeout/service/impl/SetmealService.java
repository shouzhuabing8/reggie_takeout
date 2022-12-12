package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.dto.SetmealDto;
import com.itheima.reggie_takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);
    public void updateByIdWithDish(SetmealDto setmealDto);

}
