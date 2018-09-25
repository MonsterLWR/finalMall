package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 功能描述: 获取userId用户的orderNo订单的所有订单项信息
     *
     * @param: orderNo，userId
     * @return: List<OrderItem>
     * @auther: Lee
     * @date: 2018/9/17 14:42
     */
    List<OrderItem> getByOrderNoUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    /**
     * 功能描述: 批量插入orderItemList的数据到数据库中
     *
     * @param: orderItemList
     * @return: void
     * @auther: Lee
     * @date: 2018/9/17 14:32
     */
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    /**
     * 功能描述: 获取orderNo对应的订单项信息
     *
     * @param: orderNo
     * @return: List<OrderItem>
     * @auther: Lee
     * @date: 2018/9/17 14:57
     */
    List<OrderItem> getByOrderNo(Long orderNo);
}