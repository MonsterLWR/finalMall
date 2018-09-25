package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 功能描述: 购物车功能的Controller
 *
 * @auther: Lee
 * @date: 2018/9/17 9:16
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session) {
        /**
         * 功能描述: 显示登陆用户的购物车信息
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:16
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        /**
         * 功能描述: 登陆用户添加productId对应的商品count个进入购物车
         *
         * @param: [session, count, productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:32
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        /**
         * 功能描述: 更新登录用户购物车中productId产品的数量为count
         *
         * @param: [session, count, productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:36
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds) {
        /**
         * 功能描述: 删除登陆用户购物车中多个productId对应的商品的信息。
         *
         * @param: [session, productIds]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:38
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        /**
         * 功能描述: 勾选登陆用户购物车中的所有商品
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:41
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        /**
         * 功能描述: 不勾选登陆用户购物车中的所有商品
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:45
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session, Integer productId) {
        /**
         * 功能描述: 勾选登陆用户购物车中的productId对应的商品
         *
         * @param: [session, productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:45
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session, Integer productId) {
        /**
         * 功能描述: 不勾选登陆用户购物车中的productId对应的商品
         *
         * @param: [session, productId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:45
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        /**
         * 功能描述: 获取登陆用户购物车中商品的数量
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<java.lang.Integer>
         * @auther: Lee
         * @date: 2018/9/17 9:46
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
