package com.itheima.reggie_takeout.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
/*员工表格*/
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码  要注意数据库驼峰命名

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //创建的时候自动填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //修改的时候自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
