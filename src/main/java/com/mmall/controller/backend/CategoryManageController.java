package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 功能描述: 后台分类管理Controller
 *
 * @auther: Lee
 * @date: 2018/9/16 15:07
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        /**
         * 功能描述: 添加分类，默认父分类0
         *
         * @param: [session, categoryName, parentId]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:07
         */
        //管理员权限判断
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录！");
        } else {
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //是管理员
                return iCategoryService.addCategory(categoryName, parentId);
            } else {
                return ServerResponse.createByErrorMessage("需要管理员权限！");
            }
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        /**
         * 功能描述: 更新分类名称
         *
         * @param: [session, categoryId, categoryName]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:10
         */
        //管理员权限
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        /**
         * 功能描述: 获取categoryId对应的子分类，不递归。
         *
         * @param: [session, categoryId]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:14
         */
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息,并且不递归,保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        /**
         * 功能描述: 获取categoryId对应的子分类，递归。
         *
         * @param: [session, categoryId]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 15:16
         */
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //递归查询当前id的所有子id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }
}
