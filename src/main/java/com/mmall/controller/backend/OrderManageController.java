package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        /**
         * 功能描述:获取所有订单的信息，分页返回。
         *
         * @param: [session, pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/17 15:11
         */

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        /**
         * 功能描述: 管理员获取orderNo对应订单的详细信息
         *
         * @param: [session, orderNo]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.OrderVo>
         * @auther: Lee
         * @date: 2018/9/17 15:35
         */

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充我们增加产品的业务逻辑

            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        /**
         * 功能描述: 按照订单号查询订单
         *
         * @param: [session, orderNo, pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/25 20:33
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {
        /**
         * 功能描述: 管理员设置orderNo对应订单的状态为送货状态，只有付款状态的订单可以发货
         *
         * @param: [session, orderNo]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/17 15:35
         */

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


}
