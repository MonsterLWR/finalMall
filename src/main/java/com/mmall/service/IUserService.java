package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 功能描述: 用户相关Service接口
 *
 * @auther: Lee
 * @date: 2018/9/16 10:20
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> regist(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String anwser);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);

    ServerResponse userList(int pageNum, int pageSize);
}
