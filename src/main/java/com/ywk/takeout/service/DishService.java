package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.dto.DishDto;
import com.ywk.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public R<String> deleteWithFlavor(List<Long> id);

    R<Page> page(int page, int pageSize, String name);

    R<List<DishDto>> list(Dish dish);

    R<String> updateStatus(Integer status, List<Long> ids);
}
