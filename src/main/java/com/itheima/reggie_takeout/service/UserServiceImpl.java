package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.User;
import com.itheima.reggie_takeout.mapper.UserMapper;
import com.itheima.reggie_takeout.service.impl.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
