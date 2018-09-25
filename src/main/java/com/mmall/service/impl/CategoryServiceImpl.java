package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 功能描述: 分类管理Service
 *
 * @auther: Lee
 * @date: 2018/9/16 15:08
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        /**
         * 功能描述: 添加名为categoryName的分类，父分类为parentId
         *
         * @param: [categoryName, parentId]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:08
         */
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加分类参数错误！");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //设置分类状态有效
        category.setStatus(true);

        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("分类添加成功！");
        } else {
            return ServerResponse.createByErrorMessage("分类添加失败！");
        }
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName){
        /**
         * 功能描述: 更新categoryId对应分类的名称为categoryName
         *
         * @param: [categoryId, categoryName]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:13
         */
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新分类参数错误！");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        /**
         * 功能描述: 获取categoryId对应的子分类，不递归。
         *
         * @param: [categoryId]
         * @return: com.mmall.common.ServerResponse<java.util.List<com.mmall.pojo.Category>>
         * @auther: Lee
         * @date: 2018/9/16 15:15
         */
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        /**
         * 功能描述: 获取categoryId对应的子分类，递归。
         *
         * @param: [categoryId]
         * @return: com.mmall.common.ServerResponse<java.util.List<java.lang.Integer>>
         * @auther: Lee
         * @date: 2018/9/16 15:16
         */
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法,算出子节点
    private void findChildCategory(Set<Category> categorySet ,Integer categoryId){
        /**
         * 功能描述: 将categoryId对应的子分类全部添加到categorySet
         *
         * @param: [categorySet, categoryId]
         * @return: void
         * @auther: Lee
         * @date: 2018/9/16 15:17
         */
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
    }
}
