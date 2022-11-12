package com.ywk.takeout.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywk.takeout.common.R;
import com.ywk.takeout.dto.SetmealDto;
import com.ywk.takeout.entity.Setmeal;
import com.ywk.takeout.service.SetmealDishService;
import com.ywk.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐:{}", setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询,如果套餐下面的菜有停售的或者删除的，就把套餐设置为停售
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("套餐分页查询:page = {}, pageSize = {}, name = {}", page, pageSize, name);
        return setmealService.page(page, pageSize, name);

    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐:{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public  R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("根据条件查询套餐:{}", setmeal.toString());
        return setmealService.list(setmeal);
    }

    /**
     * 修改套餐状态
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        log.info("修改套餐状态为:{},套餐号为:{}", status, ids);
        return setmealService.updateStatus(status, ids);
    }

    /**
     * 修改数据时的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        log.info("修改数据时回显:{}", id);
        SetmealDto setmealDto = setmealService.getData(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto) {
        log.info("修改套餐:{}", setmealDto.toString());
        return setmealService.edit(setmealDto);
    }

}
