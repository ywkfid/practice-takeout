package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.dto.SetmealDto;
import com.ywk.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> id);

    public SetmealDto getData(Long id);
}
