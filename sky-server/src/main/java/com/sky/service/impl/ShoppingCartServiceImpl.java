package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //只可能是套餐id和菜品id(包含口味）
        //所有用户的购物车数据都存放在一个购物车数据库中
        //首先得到当前用户的购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断添加的数据是否在购物车中
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        //若存在
        if(shoppingCartList!=null && shoppingCartList.size()>0){
            //数量加一
             shoppingCart = shoppingCartList.get(0);
             shoppingCart.setNumber(shoppingCart.getNumber()+1);
             shoppingCartMapper.updateNumberById(shoppingCart);
        }else{
            //若不存在
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId!=null){
               Dish dish =  dishMapper.getDishById(dishId);
                //插入的是菜品
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
//                shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
                shoppingCart.setAmount(dish.getPrice());

            }else{
                //输入的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }
}
