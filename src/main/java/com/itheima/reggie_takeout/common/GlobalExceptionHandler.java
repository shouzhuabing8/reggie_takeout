package com.itheima.reggie_takeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestControllerAdvice.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法  拦截到数据库异常，就进行处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        //判断错误信息，如果是Duplicate entry开头的，判定为重复信息，
        // 按空格将一段话分为几个字符串，放入数组， 重复信息在数组中顺序是第三个
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split=ex.getMessage().split(" ");
            String msg= split[2]+"已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }
    /**
     * 异常处理方法  拦截customException抛出的异常，就进行处理
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }



}
