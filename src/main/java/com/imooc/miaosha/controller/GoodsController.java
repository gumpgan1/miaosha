package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;


    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String toLogin(Model model,MiaoshaUser miaoshaUser){
        model.addAttribute("user",miaoshaUser);
        //查询商品列表
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsVoList);
        return "goods";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId")long goodsId){
        model.addAttribute("user",miaoshaUser);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);

        //获取秒杀的开始和结束时间来判断是否可以进行秒杀
        long startDate = goodsVo.getStartDate().getTime();
        long endDate = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshuStatus = 0;
        int remainSeconds = 0;
        if(now<startDate){//倒计时
            miaoshuStatus = 0;
            remainSeconds = (int) ((startDate-now)/1000);
        }else if(now > endDate){//秒杀已经结束
            miaoshuStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshuStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshuStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        return "goods_detail";
    }
}
