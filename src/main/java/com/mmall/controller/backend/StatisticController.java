package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IStatisticService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @Auther: ZhangHao
 * @Date: 2018/9/25 20:17
 * @Description:
 */

@Controller
@RequestMapping("/manage/statistic")
public class StatisticController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IStatisticService iStaticService;

    @RequestMapping("base_count.do")
    @ResponseBody
    public ServerResponse baseCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iStaticService.count();
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
