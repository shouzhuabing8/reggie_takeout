package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.DishFlavor;
import com.itheima.reggie_takeout.mapper.DishMapper;
import com.itheima.reggie_takeout.service.impl.DishFlavorService;
import com.itheima.reggie_takeout.service.impl.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //前端传过来的数据没有菜品的id，所以要从dish表中获取id遍历进去
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过输出流的方式将id输入
        /*flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());*/
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }


        //保存菜品口味数据到菜品口味表dish_flavor,应该是用于保存集合的。
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，dish表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询对应的口味信息，dish_flavor查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    //更新菜品信息和口味信息
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //更新dish表的基本信息
        this.updateById(dishDto);
        //删除菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);
        //重新添加菜品口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

/*        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());*/

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);
    }
}
