package com.itheima.reggie_takeout.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * 通过线程处理 从LoginCheckFilter中获取，然后在MetaObjecthandler中使用
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
