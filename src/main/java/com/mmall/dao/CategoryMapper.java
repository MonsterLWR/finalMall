package com.mmall.dao;

import com.mmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /**
     * 功能描述: 获取categoryId对应的子分类
     *
     * @param: parent_id
     * @return: List<Category>
     * @auther: Lee
     * @date: 2018/9/16 15:16
     */
    List<Category> selectCategoryChildrenByParentId(Integer parent_id);
}