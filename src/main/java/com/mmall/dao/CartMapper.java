package com.mmall.dao;

import com.google.common.collect.Lists;
import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);


    int updateByPrimaryKey(Cart record);

    /**
     * 功能描述: 查询userId对应的购物车中商品为productId的购物车信息
     *
     * @param: userId,productId
     * @return: Cart
     * @auther: Lee
     * @date: 2018/9/17 9:34
     */
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 功能描述: 选择userId对应的购物车记录
     *
     * @param: userId
     * @return: List<Cart>
     * @auther: Lee
     * @date: 2018/9/17 9:21
     */
    List<Cart> selectCartByUserId(Integer userId);

    /**
     * 功能描述: userId对应用户的购物车未勾选的产品个数
     *
     * @param: userId
     * @return: int
     * @auther: Lee
     * @date: 2018/9/17 9:31
     */
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     * 功能描述:删除 userId用户productId在productIds中的商品的购物信息
     *
     * @param: userId,productIds
     * @return: int
     * @auther: Lee
     * @date: 2018/9/17 9:40
     */
    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIds") List<String> productIds);

    /**
     * 功能描述: 将userId用户的购物车中productId的商品的勾选状态设置为checked，如果productId为null，则将
     *          userId用户的购物车中所有商品的勾选状态设置为checked
     *
     * @param: userId,productId,checked
     * @return: int
     * @auther: Lee
     * @date: 2018/9/17 9:44
     */
    int checkedOrUnchecked(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    /**
     * 功能描述: 返回userId对应的购物车中商品种类数量，若没有则返回0
     *
     * @param: userId
     * @return: int
     * @auther: Lee
     * @date: 2018/9/17 9:47
     */
    int selectCartProductCount(Integer userId);

    /**
     * 功能描述: 返回userId勾选的购物车的信息
     *
     * @param: userId
     * @return: List<Cart>
     * @auther: Lee
     * @date: 2018/9/17 9:50
     */
    List<Cart> selectCheckedCartByUserId(Integer userId);
}