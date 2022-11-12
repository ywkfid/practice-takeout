package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.CustomException;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.Category;
import com.ywk.takeout.entity.Dish;
import com.ywk.takeout.entity.Setmeal;
import com.ywk.takeout.mapper.CategoryMapper;
import com.ywk.takeout.service.CategoryService;
import com.ywk.takeout.service.DishService;
import com.ywk.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 重写remove方法 判断删除分类的时候 是否分类里已经有套餐或菜品关联
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            throw new CustomException("当场分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当场分类下关联了套餐，不能删除");
        }
        this.removeById(id);
    }

    /**
     * 分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page> page(int page, int pageSize) {
        //分页构造器
        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.orderByAsc(Category::getSort);
        //分页搜索
        this.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }
}
