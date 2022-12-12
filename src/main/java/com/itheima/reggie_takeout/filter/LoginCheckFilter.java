package com.itheima.reggie_takeout.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie_takeout.common.BaseContext;
import com.itheima.reggie_takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
//拦截所有
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //servletRequest转型为httpservletrequest
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //具体逻辑流程

        //1:获取本次请求的url
        String requestURL = request.getRequestURI();

        log.info("拦截到请求：{}", requestURL);

        //定义不需要处理的请求路径,一些静态页面，和登录登出功能都放行。
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录

        };
        //2:判断本次请求是否需要处理 【封装一个方法来【判断】】  (是：步骤3   否：放行)
        boolean check = check(urls, requestURL);
        //直接放放行
        if (check) {
            log.info("本次请求{}不需要处理",requestURL);
            filterChain.doFilter(request, response);
            return;
        }


        //3-1:判断是否登录（是：放行 否：返回结果{未登录}）
        //通过获取session中的用户信息来判断
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("employee"));

            Long empId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            //线程输出，另外两个在2employeeController的update方法  3MyMetaObjectHandler的updateFill方法
            //Long id = Thread.currentThread().getId();
            //log.info("线程id为:{}",id);

            filterChain.doFilter(request, response);
            return;
        }

        //3-2:用来判断用户User是否登录，   判断是否登录（是：放行 否：返回结果{未登录}）
        //通过获取session中的用户信息来判断
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("user"));

            Long userId=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            //线程输出，另外两个在2employeeController的update方法  3MyMetaObjectHandler的updateFill方法
            //Long id = Thread.currentThread().getId();
            //log.info("线程id为:{}",id);

            filterChain.doFilter(request, response);
            return;
        }


        //未登录   前端的request.js做了前端的拦截，只需要返回相应的数据，使得前端可以判断该页面需要拦截
        //通过JSON输出流的方式返回，R中的错误信息 是NOTLOGIN要与前端的保持一致。
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
        //用日志方式输出
        //log.info("拦截到请求：{}", request.getRequestURI());
    }

    //路径匹配，检查是否要放行
    public boolean check(String[] urls, String requestURL) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
