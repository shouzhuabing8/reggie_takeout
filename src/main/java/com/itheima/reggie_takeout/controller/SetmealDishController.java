package com.itheima.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.dto.SetmealDto;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.entity.SetmealDish;
import com.itheima.reggie_takeout.service.impl.CategoryService;
import com.itheima.reggie_takeout.service.impl.SetmealDishService;
import com.itheima.reggie_takeout.service.impl.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealDishController{
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save (@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }
    /**
     * 分页查询
     *
     * 需要的是分类的名称，如果查setmeal类型的话，得不到
     * 要将setmeal拷贝到setmealdto中，然后通过其id在category查到名称
     */
    @GetMapping("/page")
    public R<Page> page (@RequestParam int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageinfo=new Page<>(page,pageSize);
        Page<SetmealDto> SetmealDtoPage=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.hasLength(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageinfo, queryWrapper);

        //对象拷贝，将不需要拷贝的放在第三个位置
        BeanUtils.copyProperties(pageinfo, SetmealDtoPage, "records");

        List<Setmeal> records = pageinfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        /*List<SetmealDto> list = new ArrayList<SetmealDto>();

        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            Category category = categoryService.getById(record.getCategoryId());

            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            list.add(setmealDto);
        }*/
        SetmealDtoPage.setRecords(list);


        return R.success(SetmealDtoPage);
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> list(@RequestParam Long categoryId , @RequestParam int status){
        //
        //条件构造
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(Setmeal::getStatus,status);

        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete (@RequestParam List<Long> ids){
        /*//批量起售和停售都是前端做的，批量删除是后端做的
        if (ids == null) {
            throw new CustomException("批量操作，请先勾选操作菜品！");
        }*/

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    /**
     * 数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateByIdWithDish(setmealDto);
        return R.success("修改套餐ok");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> updateStatus (@PathVariable int status,@RequestParam List<Long> ids){
        for (Setmeal setmeal : setmealService.listByIds(ids)) {
            setmeal.setStatus(status);
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,setmeal.getId());
            setmealService.update(setmeal,queryWrapper);
        }
        return R.success("修改状态成功");
    }
}
