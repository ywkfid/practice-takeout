package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.BaseContext;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.ShoppingCart;
import com.ywk.takeout.mapper.ShoppingCartMapper;
import com.ywk.takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是那个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        //查询当前菜品或者是套餐是否在购物车当中
        if (dishId != null) {
            //添加的是菜品id
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
//            前端不支持口味分开，添加第二份的时候，不允许选口味
//            queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = this.getOne(queryWrapper);
        //如果已经存在（口味这些都一样），就在原来的基础上加一
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.updateById(cartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        //否则在表中加一条数据，数量默认是1
        return R.success(cartServiceOne);
    }

    /**
     * 购物车套餐减去
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> sub(ShoppingCart shoppingCart) {

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        //代表数量减少的是菜品数量
        if (dishId != null){
            //通过dishId查出购物车对象
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
            queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart cart1 = this.getOne(queryWrapper);
            cart1.setNumber(cart1.getNumber()-1);
            Integer LatestNumber = cart1.getNumber();
            if (LatestNumber > 0){
                //对数据进行更新操作
                this.updateById(cart1);
            }else if(LatestNumber == 0){
                //如果购物车的菜品数量减为0，那么就把菜品从购物车删除
                this.removeById(cart1.getId());
            }else if (LatestNumber < 0){
                return R.error("操作异常");
            }
            return R.success(cart1);
        }

        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null){
            //代表是套餐数量减少
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId).eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            ShoppingCart cart2 = this.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber()-1);
            Integer LatestNumber = cart2.getNumber();
            if (LatestNumber > 0){
                //对数据进行更新操作
                this.updateById(cart2);
            }else if(LatestNumber == 0){
                //如果购物车的套餐数量减为0，那么就把套餐从购物车删除
                this.removeById(cart2.getId());
            }else if (LatestNumber < 0){
                return R.error("操作异常");
            }
            return R.success(cart2);
        }
        //如果两个大if判断都进不去
        return R.error("操作异常");
    }
}
