package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.CustomException;
import com.ywk.takeout.common.R;
import com.ywk.takeout.dto.DishDto;
import com.ywk.takeout.entity.Category;
import com.ywk.takeout.entity.Dish;
import com.ywk.takeout.entity.DishFlavor;
import com.ywk.takeout.mapper.DishMapper;
import com.ywk.takeout.service.CategoryService;
import com.ywk.takeout.service.DishFlavorService;
import com.ywk.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 添加菜品同时保持口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品和口味
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品，要看是否绑定套餐
     * @param id
     */
    @Override
    public R<String> deleteWithFlavor(List<Long> id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, id);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish: list) {
            if (dish.getStatus() == 1) {
                throw new CustomException("此菜正在售货，不能删除");
            }
            this.removeById(dish);
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId, id);
        dishFlavorService.remove(queryWrapper1);
        return R.success("删除菜品成功");
    }

    /**
     * 菜品分页信息查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        this.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }
    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
    @Override
    public R<List<DishDto>> list(Dish dish) {
        //先要查dish表 把起售的查出来
        //select* from dish where category_Id=? ,status=1 orderBy...
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq( dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes = this.list(lambdaQueryWrapper);
        List<DishDto> dishDtoList =dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //这次把每个item拷贝，并且要存在dishDto中
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //一定要注意这个category 的空指针异常 因为有的记录里面没有category 该字段不为空的才赋值
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            //select * from dish_flavor where dish_Id= ?
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            //得到口味表的集合
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            //再set进dishDto里面
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    /**
     * 更新菜品状态
     * @param status
     * @param ids
     * @return
     */
    @Override
    public R<String> updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish: list) {
            if (dish != null) {
                dish.setStatus(status);
                this.updateById(dish);
            }
        }
        return R.success("售卖状态更新成功");
    }
}
