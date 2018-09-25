package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.UserMapper;
import com.mmall.service.IStatisticService;
import com.mmall.vo.CountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: Lee
 * @Date: 2018/9/25 20:21
 * @Description:
 */
@Service("iStaticService")
public class StatisticServiceImpl implements IStatisticService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse count() {
        CountVo countVo = new CountVo();
        countVo.setOrderCount(orderMapper.selectCount());
        countVo.setUserCount(userMapper.selectCount());
        countVo.setProductCount(productMapper.selectCount());
        return ServerResponse.createBySuccess(countVo);
    }
}
