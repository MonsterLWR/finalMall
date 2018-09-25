package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    /**
     * 功能描述: 获取userId对应的orderNo的订单信息
     *
     * @param: userId，orderNo
     * @return: Order
     * @auther: Lee
     * @date: 2018/9/17 14:41
     */
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    /**
     * 功能描述: 获取orderNo对应的订单的订单信息
     *
     * @param: orderNo
     * @return: Order
     * @auther: Lee
     * @date: 2018/9/17 15:25
     */
    Order selectByOrderNo(Long orderNo);

    /**
     * 功能描述: 获取userId对应的所有订单信息
     *
     * @param: userId
     * @return: List<Order>
     * @auther: Lee
     * @date: 2018/9/17 15:03
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 功能描述: 获取所有订单信息
     *
     * @param:
     * @return: List<Order>
     * @auther: Lee
     * @date: 2018/9/17 15:12
     */
    List<Order> selectAllOrder();

    /**
     * 功能描述: 订单总数
     *
     * @param:
     * @return:
     * @auther: Lee
     * @date: 2018/9/25 20:25
     */
    Integer selectCount();
}