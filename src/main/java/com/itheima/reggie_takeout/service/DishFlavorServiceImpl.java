package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.DishFlavor;
import com.itheima.reggie_takeout.mapper.DishFlavorMapper;
import com.itheima.reggie_takeout.service.impl.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
