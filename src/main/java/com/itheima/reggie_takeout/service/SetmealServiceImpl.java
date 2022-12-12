package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.dto.SetmealDto;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.entity.SetmealDish;
import com.itheima.reggie_takeout.mapper.SetmealMapper;
import com.itheima.reggie_takeout.service.impl.SetmealDishService;
import com.itheima.reggie_takeout.service.impl.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，保存套餐和菜品的关联关系
     * 加Transactional注解，保证事务的一致性，要么同时成功，要么同时失败
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);


        List<SetmealDish> setmealDishes =setmealDto.getSetmealDishes();

        //通过steam流 将setmealDto的id传入 setSetmealId
/*        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());*/

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，并删除相关的菜品类
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {


        //查询状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //如果不能，抛出业务异常
        if (count != 0) {
            throw new CustomException("有套餐正在售卖中，不能删除");
        }
        //如果可以，先删除套餐表数据
        this.removeByIds(ids);
        //删除关系表数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {

        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);
        //Setmeal setmeal = this.list(queryWrapper);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishes=setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    public void updateByIdWithDish(SetmealDto setmealDto) {
        //修改套餐的基本信息，操作setmeal，执行insert操作,注意一定要是updatebyid，通过id来找到对应的数据
        this.updateById(setmealDto);

        //如果通过建立条件查询器，通过查询setmeal的id去匹配，那么一次将会匹配多个，所以还是用添加的方法，将相关数据先删除，再添加
        LambdaQueryWrapper<SetmealDish> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes =setmealDto.getSetmealDishes();

        //通过steam流 将setmealDto的id传入 setSetmealId
/*        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());*/

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }


}
