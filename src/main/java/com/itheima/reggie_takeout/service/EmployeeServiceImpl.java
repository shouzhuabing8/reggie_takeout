package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.Employee;
import com.itheima.reggie_takeout.mapper.EmployeeMapper;
import com.itheima.reggie_takeout.service.impl.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
