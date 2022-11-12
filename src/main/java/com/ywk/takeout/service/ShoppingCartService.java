package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> add(ShoppingCart shoppingCart);

    R<ShoppingCart> sub(ShoppingCart shoppingCart);

}
