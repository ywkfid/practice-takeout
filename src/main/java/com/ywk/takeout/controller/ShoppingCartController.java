package com.ywk.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywk.takeout.common.BaseContext;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.ShoppingCart;
import com.ywk.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 展示购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> get() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);
        return shoppingCartService.add(shoppingCart);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);

        return R.success("清除购物车成功");
    }

    /**
     * 客户端的套餐或者是菜品数量减少设置
     * 没必要设置返回值
     * @param shoppingCart
     */
    @PostMapping("/sub")
    @Transactional
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("套餐或菜品减少:{}", shoppingCart.toString());
        return shoppingCartService.sub(shoppingCart);
    }

}
