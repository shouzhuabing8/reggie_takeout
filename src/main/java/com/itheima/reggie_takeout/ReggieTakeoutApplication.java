package com.itheima.reggie_takeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
//slf4j用于日志  使用户可以在运行时嵌入他们想使用的日志框架。
@SpringBootApplication
@ServletComponentScan//添加注解 ，去扫描拦截器配置类
@EnableTransactionManagement
public class ReggieTakeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeoutApplication.class, args);
        log.info("项目启动成功");
    }


}
