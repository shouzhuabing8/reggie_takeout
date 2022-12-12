package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.mapper.CategoryMapper;
import com.itheima.reggie_takeout.service.impl.CategoryService;
import com.itheima.reggie_takeout.service.impl.DishService;
import com.itheima.reggie_takeout.service.impl.EmployeeService;
import com.itheima.reggie_takeout.service.impl.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果关联，抛出一个异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount=dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果关联，抛出一个异常
        if (dishCount>0){
            //有菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经管理，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount= setmealService.count(setmealLambdaQueryWrapper);

        if(setmealCount>0){
            //有菜单，抛出异常
            throw new CustomException("当前分类下关联了菜单，不能删除");
        }

        //正常删除分类  通过super调用本类的构造方法
        super.removeById(id);
    }
}
