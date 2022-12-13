package com.itheima.reggie_takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.DishFlavor;
import com.itheima.reggie_takeout.service.impl.CategoryService;
import com.itheima.reggie_takeout.service.impl.DishFlavorService;
import com.itheima.reggie_takeout.service.impl.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        //清理所有缓存数据
        //Set keys=redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("添加菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(@RequestParam int page, int pageSize, String name) {
        //分页构造器
        Page<Dish> pageinfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //条件构造器根据姓名查询的时候 必须先保证字段不为空，才去比较条件字段。
        queryWrapper.like(StringUtils.hasLength(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = dishService.page(pageinfo, queryWrapper);

        //对象拷贝，将不需要拷贝的放在第三个位置
        BeanUtils.copyProperties(pageinfo, dishDtoPage, "records");


        //去处理records,通过stream流的方式
/*        List<Dish> records = pageinfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            //根据id查询
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);*/
        List<Dish> records = pageinfo.getRecords();
        List<DishDto> list = new ArrayList<DishDto>();

        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);
            Category category = categoryService.getById(record.getCategoryId());

            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            list.add(dishDto);
        }
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);

        //清理所有缓存数据
        //Set keys=redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);


        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询
     * @param dish
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //参数传的是 id 但是用dish接收会更加通用
        //条件构造
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList =null;
        String key ="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }
        //如果不存在，查询数据库，将数据库中缓存到redis





        //参数传的是 id 但是用dish接收会更加通用
        //条件构造
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，查询数据库，将数据库中缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus (@PathVariable int status,@RequestParam List<Long> ids){
        for (Dish dish : dishService.listByIds(ids)) {
            dish.setStatus(status);
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId,dish.getId());
            dishService.update(dish,queryWrapper);
        }
        return R.success("修改状态成功");
    }

}
