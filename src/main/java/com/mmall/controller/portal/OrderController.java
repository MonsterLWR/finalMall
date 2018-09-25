package com.mmall.controller.portal;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        /**
         * 功能描述:登陆用户使用shippingId对应的收货地址创建订单。
         *
         * @param: [session, shippingId]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 11:20
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo) {
        /**
         * 功能描述: 获取登陆用户对应orderNo的详情信息
         *
         * @param: [session, orderNo]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 14:40
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {
        /**
         * 功能描述: 若登陆用户的orderNo订单还未付款，则取消该订单
         *
         * @param: [session, orderNo]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 14:46
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }


    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session) {
        /**
         * 功能描述: 获取登陆用户当前购物车中勾选产品并拟生成订单。
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 14:48
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        /**
         * 功能描述: 获取登录用户的所有订单信息，结果做分页处理
         *
         * @param: [session, pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 14:54
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }


    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        /**
         * 功能描述: 向支付宝提出支付申请
         *
         * @param: [session, orderNo, request]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/17 15:36
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        /**
         * 功能描述: 支付宝回调接口，根据支付宝的回调，进行验签之后改变订单信息。
         *
         * @param: [request]
         * @return: java.lang.Object
         * @auther: Lee
         * @date: 2018/9/17 15:56
         */
        Map<String, String> params = Maps.newHashMap();

        //用请求中的数据构造params
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //验证回调的正确性,是不是支付宝发的.并且还要避免重复通知.
        //验签
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,验证不通过！");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }

        //处理支付宝响应
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        /**
         * 功能描述: 查询登录用户的orderNo对应订单的支付状态
         *
         * @param: [session, orderNo]
         * @return: com.mmall.common.ServerResponse<java.lang.Boolean>
         * @auther: Lee
         * @date: 2018/9/17 15:59
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


}
