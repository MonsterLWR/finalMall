package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 功能描述: 选择所有产品
     *
     * @param:
     * @return: List<Product>
     * @auther: Lee
     * @date: 2018/9/16 17:04
     */
    List<Product> selectList();

    /**
     * 功能描述: 使用productName做关键字或者productId查询product
     *
     * @param: productName，productId
     * @return: List<Product>
     * @auther: Lee
     * @date: 2018/9/16 17:07
     */
    List<Product> selectByNameAndProductId(@Param("productName") String productName, @Param("productId") Integer productId);

    /**
     * 功能描述: 以categoryIds或关键字productName查询商品
     *
     * @param: productName,categoryIds
     * @return:
     * @auther: Lee
     * @date: 2018/9/16 16:09
     */
    List<Product> selectByNameAndCategoryIds(@Param("productName") String productName, @Param("categoryIds") List<Integer> categoryIds);

    /**
     * 功能描述: 商品总数
     *
     * @param:
     * @return:
     * @auther: Lee
     * @date: 2018/9/25 20:25
     */
    Integer selectCount();
}