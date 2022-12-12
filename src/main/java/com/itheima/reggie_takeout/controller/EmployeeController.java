package com.itheima.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.entity.Employee;
import com.itheima.reggie_takeout.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         1页面密码md5加密
         2根据提交用户名查数据库
         3没查询用户名到返回登录失败
         4查到密码不对返回密码不对
         5查看员工状态是否已禁用（status[0/1]）
         6都对，将员工id放入session，返回成功结果
         */
        //1页面密码md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //2根据提交用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//数据库有唯一约束
        //3没查询用户名到返回登录失败
        if (emp==null){
            return R.error("登录失败");
        }
        //4查到密码不对返回密码不对
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }
        //5查看员工状态是否已禁用（status[0/1]）
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        //6都对，将员工id放入session，返回成功结果
        //通过request获取session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        /**
         * 1清理session中的用户id
         * 2返回结果
         */
        //1清理session中的用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());
        //密码，每个人都给个初始密码,md5加密的
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //除了前端接收的数据，其他的数据需要在后端添加
        //status 在数据库设置了默认值，可以不用添加
        //id 是用雪花算法自动生成的
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得request对象，获得当前用户的id
        //Long empId=(Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page 第几页
     * @param pageSize 每页多少条数据
     * @param name  搜索框提交的信息
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo =new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        //视频上是用StringUtils.isNotEmpty来判断非空，我用的hasLength
        queryWrapper.like(StringUtils.hasLength(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);


        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        log.info(employee.toString());

        Long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);

        employeeService.updateById(employee);

        return R.success("修改数据成功");
    }
    @GetMapping("/{id}")
    //从路径中获取信息
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
