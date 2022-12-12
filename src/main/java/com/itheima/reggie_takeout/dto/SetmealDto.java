package com.itheima.reggie_takeout.dto;

import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
