package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.MD5Util;
import com.mmall.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 功能描述: 用户相关Service实现
 *
 * @auther: Lee
 * @date: 2018/9/16 10:21
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        /**
         * 功能描述: 根据username和password进行用户登录验证，password会使用MD5编码。
         *
         * @param: [username, password]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: Lee
         * @date: 2018/9/16 10:22
         */
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户民不存在！");
        }

        //编码password
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        //检查用户名密码
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误！");
        }

        //密码置空，返回给前端
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> regist(User user) {
        /**
         * 功能描述: 检查注册的用户名，邮箱是否重复，并添加注册信息到数据库。
         *
         * @param: [user]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 10:35
         */
        //检验用户名重复
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //检验邮箱重复
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败!");
        }
        return ServerResponse.createBySuccessMessage("注册成功！");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        /**
         * 功能描述: 根据type检查对应的str是否重复。type为Const.USERNAME和Const.EMAIL。
         *
         * @param: [str, type]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 10:37
         */
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户民已存在！");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("Email已存在！");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccessMessage("校验OK！");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        /**
         * 功能描述: 检查username是否存在，获取username的密码找回问题。
         *
         * @param: [username]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 10:53
         */
        //判断用户名是否合法
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在！");
        } else {
            //获取密码找回问题
            String question = userMapper.selectQuestionByUsername(username);
            if (StringUtils.isNoneBlank(question)) {
                return ServerResponse.createBySuccess(question);
            }
            return ServerResponse.createByErrorMessage("密码找回问题为空！");
        }
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String anwser) {
        /**
         * 功能描述: 检验密码找回问题的答案是否正确
         *
         * @param: [username, question, anwser]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 11:04
         */
        int resultCount = userMapper.checkAnswer(username, question, anwser);
        if (resultCount > 0) {
            //生成随机token并添加到本地缓存，增安全性，防止直接调用重置密码接口
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案不正确！");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        /**
         * 功能描述: 通过回答密码找回问题，需要传token
         *
         * @param: [username, passwordNew, forgetToken]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 11:07
         */
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("Token未传递！参数错误！");
        }

        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在！");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("Token已过期！");
        }

        if (StringUtils.equals(token, forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int updateCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (updateCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("Token错误！请重试！");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        /**
         * 功能描述: 登录状态下重置密码，需要传旧密码
         *
         * @param: [passwordOld, passwordNew, user]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 11:09
         */
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误！");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("更改密码成功！");
        }
        return ServerResponse.createByErrorMessage("更新密码失败！");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        /**
         * 功能描述: 更新user的个人信息
         *
         * @param: [user]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: Lee
         * @date: 2018/9/16 11:13
         */
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已存在！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败！");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        /**
         * 功能描述: 获取userid的用户信息
         *
         * @param: [userId]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.User>
         * @auther: Lee
         * @date: 2018/9/16 11:15
         */
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        /**
         * 功能描述: 检查user是否为管理员角色
         *
         * @param: [user]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 11:16
         */
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse userList(int pageNum, int pageSize) {
        /**
         * 功能描述: 获取用户列表
         *
         * @param: []
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/25 19:35
         */
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectUsers();
        List<UserVo> userVoList = assembleUserVoList(userList);
        PageInfo pageInfo = new PageInfo(userList);
        pageInfo.setList(userVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<UserVo> assembleUserVoList(List<User> userList) {
        /**
         * 功能描述: 构建userVo
         *
         * @param: [userList]
         * @return: java.util.List<com.mmall.vo.UserVo>
         * @auther: Lee
         * @date: 2018/9/25 19:57
         */
        List<UserVo> userVoList = Lists.newArrayList();
        for (User user : userList) {
            UserVo userVo = new UserVo();
            userVo.setId(user.getId());
            userVo.setEmail(user.getEmail());
            userVo.setPhone(user.getPhone());
            userVo.setUsername(user.getUsername());
            userVo.setCreateTime(DateTimeUtil.dateToStr(user.getCreateTime()));
            userVoList.add(userVo);
        }
        return userVoList;
    }
}
