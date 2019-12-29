package com.imooc.miaosha.service;


import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    //减库存，下订单，写入秒杀订单三个是原子操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goods) {
        goodsService.reduceStock(goods);
        //创建秒杀订单，包括order_info & miaosha_order两张表
        return orderService.creatOrder(miaoshaUser,goods);


    }
}
