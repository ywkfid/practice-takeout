package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.dto.DishDto;
import com.ywk.takeout.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(Long id);
}
