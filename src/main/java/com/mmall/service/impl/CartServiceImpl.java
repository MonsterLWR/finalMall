package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能描述: 购物车功能的业务逻辑实现
 *
 * @auther: Lee
 * @date: 2018/9/17 9:17
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        /**
         * 功能描述: 根据userId获取该用户的购物车信息
         *
         * @param: [userId]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:17
         */
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        /**
         * 功能描述: userId用户添加productId对应的商品count个进入购物车
         *
         * @param: [userId, productId, count]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:33
         */
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    private CartVo getCartVoLimit(Integer userId) {
        /**
         * 功能描述: 根据userId获取该用户的购物车信息,组装成CartVo返回。
         *
         * @param: [userId]
         * @return: com.mmall.vo.CartVo
         * @auther: Lee
         * @date: 2018/9/17 9:18
         */
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //总价
        BigDecimal cartTotalPrice = new BigDecimal("0");


        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                //从cart获取cartProductVo的信息
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    //从对应的product获取cartProductVo的信息
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        //库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //更新购物车中库存为当前剩余的库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //设置vo的库存
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算该商品所需价格
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
//                    System.out.println(cartTotalPrice.doubleValue());
//                    System.out.println(cartProductVo.getProductTotalPrice().doubleValue());
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        /**
         * 功能描述: 判断userId的购物车是否全部勾选
         *
         * @param: [userId]
         * @return: boolean
         * @auther: Lee
         * @date: 2018/9/17 9:30
         */
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
        /**
         * 功能描述: 更新userId用户购物车中productId产品的数量为count
         *
         * @param: [userId, productId, count]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:36
         */
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds){
        /**
         * 功能描述: 删除userId用户购物车中多个productId对应的商品的信息。
         *
         * @param: [userId, productIds]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:38
         */
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked){
        /**
         * 功能描述: 将userId用户的购物车中productId的商品的勾选状态设置为checked，如果productId为null，则将
         * userId用户的购物车中所有商品的勾选状态设置为checked
         *
         * @param: [userId, productId, checked]
         * @return: com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         * @auther: Lee
         * @date: 2018/9/17 9:42
         */
        cartMapper.checkedOrUnchecked(userId,productId,checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        /**
         * 功能描述: 返回userId对应的购物车中商品种类数量，若没有则返回0
         *
         * @param: [userId]
         * @return: com.mmall.common.ServerResponse<java.lang.Integer>
         * @auther: Lee
         * @date: 2018/9/17 9:49
         */
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }
}
