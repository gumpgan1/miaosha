package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaUserService miaoshaUserService;


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String toLogin(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")  long goodsId){
        if(miaoshaUser == null)
            return "login";

        model.addAttribute("user",miaoshaUser);

        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <0 ){
            model.addAttribute("errmsp", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(miaoshaOrder!=null){
            model.addAttribute("errmsp", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //如果都符合条件-减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }


}
