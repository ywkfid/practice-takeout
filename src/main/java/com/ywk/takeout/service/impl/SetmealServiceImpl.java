package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.CustomException;
import com.ywk.takeout.common.R;
import com.ywk.takeout.dto.SetmealDto;
import com.ywk.takeout.entity.Category;
import com.ywk.takeout.entity.Dish;
import com.ywk.takeout.entity.Setmeal;
import com.ywk.takeout.entity.SetmealDish;
import com.ywk.takeout.mapper.SetmealMapper;
import com.ywk.takeout.service.CategoryService;
import com.ywk.takeout.service.DishService;
import com.ywk.takeout.service.SetmealDishService;
import com.ywk.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param id
     */
    @Override
    public void removeWithDish(List<Long> id) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, id);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if(count > 0) {
            throw new CustomException("套餐正在售卖，无法删除");
        }
        this.removeByIds(id);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, id);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 修改套餐的时候回显数据
     * @param id
     * @return
     */
    @Override
    public SetmealDto getData(Long id) {

        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //在关联表中查询，setmealdish
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }
        return null;
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<Setmeal>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageInfo,lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //设置套餐名字
            if(category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
            List<SetmealDish> list1 = setmealDishService.list(queryWrapper);
            //看套餐里的菜是否已经被删除或者停售，如果是则把套餐状态改为停售
            for (SetmealDish setmealDish : list1) {
                LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(Dish::getId, setmealDish.getDishId());
                Dish dish = dishService.getOne(queryWrapper1);
                if (dish == null || dish.getStatus() == 0) {
                    setmealDto.setStatus(0);
                    break;
                }
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 根据条件查询
     * @param setmeal
     * @return
     */
    @Override
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != 0, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = this.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @Override
    public R<String> updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId, ids);
        List<Setmeal> list = this.list(queryWrapper);
        for (Setmeal setmeal: list) {
            if (setmeal != null) {
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
        }
        return R.success("售卖状态更新成功");
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @Override
    public R<String> edit(SetmealDto setmealDto) {
        if (setmealDto == null) {
            return R.error("请求异常");
        }

        if (setmealDto.getSetmealDishes() == null) {
            return R.error("套餐没有菜品,请添加套餐");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        //为setmeal_dish表填充相关的属性
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
        this.updateById(setmealDto);
        return R.success("套餐修改成功");
    }
}
