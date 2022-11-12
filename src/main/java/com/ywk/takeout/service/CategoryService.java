package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);

    public R<Page> page(int page, int pageSize);
}
