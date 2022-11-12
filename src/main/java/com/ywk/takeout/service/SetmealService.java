package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.dto.SetmealDto;
import com.ywk.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> id);

    SetmealDto getData(Long id);

    R<Page> page(int page, int pageSize, String name);

    R<List<Setmeal>> list(Setmeal setmeal);

    R<String> updateStatus(Integer status, List<Long> ids);

    R<String> edit(SetmealDto setmealDto);
}
