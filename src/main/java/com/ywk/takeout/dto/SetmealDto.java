package com.ywk.takeout.dto;

import com.ywk.takeout.entity.Setmeal;
import com.ywk.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
