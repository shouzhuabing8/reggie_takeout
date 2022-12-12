package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.entity.Orders;


public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
