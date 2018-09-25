package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: ProductService接口实现
 *
 * @auther: Lee
 * @date: 2018/9/16 17:11
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        /**
         * 功能描述: 根据product中id值是否为空，来进行更新或插入操作
         *
         * @param: [product]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 16:54
         */
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                //设置主图
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }

            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        /**
         * 功能描述: 设置productId对应商品的status
         *
         * @param: [productId, status]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 17:00
         */
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        /**
         * 功能描述: 获取productId对应商品的详情
         *
         * @param: [productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.ProductDetailVo>
         * @auther: Lee
         * @date: 2018/9/16 17:03
         */
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        /**
         * 功能描述: 根据product对象组装ProductDetailVo
         *
         * @param: [product]
         * @return: com.mmall.vo.ProductDetailVo
         * @auther: Lee
         * @date: 2018/9/16 14:55
         */
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        //得到父分类
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            //该产品暂无父分类，默认为0
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //将时间戳转换为年月日的格式
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        /**
         * 功能描述: 得到所有产品信息，结果为ProductListVo，并做分页处理。
         *
         * @param: [pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/16 17:05
         */
        //使用PageHelper分页
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product) {
        /**
         * 功能描述: 通过product组装ProductListVo对象
         *
         * @param: [product]
         * @return: com.mmall.vo.ProductListVo
         * @auther: Lee
         * @date: 2018/9/16 16:10
         */
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        /**
         * 功能描述: 使用productName做关键字或者productId查询product，结果为分页的ProductListVo
         *
         * @param: [productName, productId, pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/16 17:09
         */
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = "%" + productName + "%";
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        /**
         * 功能描述: 根据productId，返回对应的ProductDetailVo,只有处于销售状态的产品才能显示。
         *
         * @param: [productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.ProductDetailVo>
         * @auther: Lee
         * @date: 2018/9/16 14:54
         */
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        /**
         * 功能描述: 根据关键字和分类Id查找商品，结果做了分页处理。orderBy支持"price_desc", "price_asc"。
         *
         * @param: [keyword, categoryId, pageNum, pageSize, orderBy]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/16 16:02
         */
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类,并且还没有关键字,返回一个空的结果集,不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //获取categoryId的所有子分类
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }

        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        //通过null去除无效值的影响
        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList
        );

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
