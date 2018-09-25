package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    /**
     * 功能描述: 删除数据库中userId和shippingId对应的收货地址记录
     *
     * @param: userId,shippingId
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 14:16
     */
    int deleteByShippingIdUserId(@Param("userId")Integer userId,@Param("shippingId")Integer shippingId);

    /**
     * 功能描述: 更新收货地址
     *
     * @param: record
     * @return: int
     * @auther: Lee
     * @date: 2018/9/16 14:19
     */
    int updateByShipping(Shipping record);

    /**
     * 功能描述: 根据userId和shippingId查询收货地址
     *
     * @param: userId,shippingId
     * @return: Shipping
     * @auther: Lee
     * @date: 2018/9/16 14:29
     */
    Shipping selectByShippingIdUserId(@Param("userId")Integer userId,@Param("shippingId")Integer shippingId);

    /**
     * 功能描述: 根据userId查询收货地址
     *
     * @param: userId
     * @return: List<Shipping>
     * @auther: Lee
     * @date: 2018/9/16 14:37
     */
    List<Shipping> selectByUserId(Integer userId);
}
