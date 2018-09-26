package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 功能描述: 前台用户Controller类
 *
 * @auther: ZhangHao
 * @date: 2018/9/16 10:18
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        /**
         *
         * 功能描述: 用户登录
         *
         * @param: [username, password, session]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:14
         */
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }


    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session) {
        /**
         * 功能描述: 登出
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:35
         */
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        /**
         * 功能描述: 注册功能
         *
         * @param: [user]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:35
         */
        return iUserService.regist(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        /**
         * 功能描述: 根据type检查对应的str是否重复。type为Const.USERNAME和Const.EMAIL。
         *
         * @param: [str, type]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:50
         */
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        /**
         * 功能描述: 判断用户登录状态，从session中获取用户信息。
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:50
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录！无法获取信息！");
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        /**
         * 功能描述: 
         *
         * @param: [username]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 10:51
         */
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        /**
         * 功能描述: 检验密码找回问题的答案是否正确
         *
         * @param: [username, question, answer]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 11:03
         */
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {
        /**
         * 功能描述: 通过回答密码找回问题重置密码，需要传token
         *
         * @param: [username, passwordNew, forgetToken]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 11:08
         */
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, HttpSession session) {
        /**
         * 功能描述: 登录状态下重置密码，需要传旧密码
         *
         * @param: [passwordOld, passwordNew, session]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: ZhangHao
         * @date: 2018/9/16 11:09
         */
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录！");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session, User user) {
        /**
         * 功能描述: 更新登录用户的个人信息。
         *
         * @param: [session, user]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: ZhangHao
         * @date: 2018/9/16 11:13
         */
        User curUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (curUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录！");
        }

        //设置session中的id，防止修改他人信息。
        user.setId(curUser.getId());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(curUser.getUsername());
            response.getData().setRole(curUser.getRole());
            response.getData().setPassword(StringUtils.EMPTY);
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session) {
        /**
         * 功能描述: 从数据库获取登录用户的个人信息
         *
         * @param: [session]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: ZhangHao
         * @date: 2018/9/16 11:15
         */
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "status=10,未登录,需要强制登录.");
        }

        ServerResponse<User> response = iUserService.getInformation(currentUser.getId());
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }
}
