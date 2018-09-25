package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能描述: 商品前台Controller
 *
 * @auther: Lee
 * @date: 2018/9/16 14:50
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {
        /**
         * 功能描述: 根据productId，返回对应的ProductDetailVo。只有处于销售状态的产品才能显示。
         *
         * @param: [productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.ProductDetailVo>
         * @auther: Lee
         * @date: 2018/9/16 14:50
         */
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        /**
         * 功能描述: 根据关键字和分类Id查找商品，结果做了分页处理。orderBy支持"price_desc", "price_asc"。
         *
         * @param: [keyword, categoryId, pageNum, pageSize, orderBy]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/16 15:57
         */
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
