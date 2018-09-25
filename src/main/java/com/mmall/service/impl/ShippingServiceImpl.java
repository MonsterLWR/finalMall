package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 功能描述: 收货地址接口实现
 *
 * @auther: Lee
 * @date: 2018/9/16 14:20
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {


    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        /**
         * 功能描述: 向user_id代表的用户添加收货地址
         *
         * @param: [userId, shipping]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 14:12
         */
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            //返回收货地址id给前端
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse<String> del(Integer userId, Integer shippingId) {
        /**
         * 功能描述: 删除对应userId和shippingId的收货地址
         *
         * @param: [userId, shippingId]
         * @return: com.mmall.common.ServerResponse<java.lang.String>
         * @auther: Lee
         * @date: 2018/9/16 14:14
         */
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        /**
         * 功能描述: 若shipping对象中的userId与参数对应，则更新该shipping
         *
         * @param: [userId, shipping]
         * @return: com.mmall.common.ServerResponse
         * @auther: Lee
         * @date: 2018/9/16 14:17
         */
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        /**
         * 功能描述: 根据userId和shippingId查询收货地址
         *
         * @param: [userId, shippingId]
         * @return: com.mmall.common.ServerResponse<com.mmall.pojo.Shipping>
         * @auther: Lee
         * @date: 2018/9/16 14:21
         */
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询地址成功", shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        /**
         * 功能描述: 分页返回userId对应的收货地址
         *
         * @param: [userId, pageNum, pageSize]
         * @return: com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         * @auther: Lee
         * @date: 2018/9/16 14:31
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
