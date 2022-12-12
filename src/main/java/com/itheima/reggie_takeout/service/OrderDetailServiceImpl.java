package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.OrderDetail;
import com.itheima.reggie_takeout.mapper.OrderDetailMapper;
import com.itheima.reggie_takeout.service.impl.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper,OrderDetail> implements OrderDetailService{
}
