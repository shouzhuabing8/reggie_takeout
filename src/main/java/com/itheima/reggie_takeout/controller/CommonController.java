package com.itheima.reggie_takeout.controller;

import com.itheima.reggie_takeout.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String bashPath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file 的命名有具体要求，必须和前端文件名保持一致。不然会报错
        //file是临时文件，需要转存到指定位置，不然运行过去文件就丢了

        //原始文件名  为防止文件名重复造成后面文件覆盖前面文件。我们只截取原文件名的后缀(.jpg)
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid重新生成文件名。防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象，如果目录在电脑中不存在。则创建一个
        File dir = new File(bashPath);
        if (!dir.exists()){
            dir.mkdir();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(bashPath+fileName));
        } catch (IOException e){
            e.printStackTrace();
        }

        return R.success(fileName);

    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(bashPath+name));
            //输出流，将文件写回浏览器。在浏览器进行展示
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            //通过输入流的读和输出流的写将文件下载完成
            int len =0;
            byte[] bytes =new byte[1024];
            while ((len=fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
