package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*, mg.stock_count, mg.miaosha_price,mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.id = g.id")
    public List<GoodsVo> getGoodsVoList();

    @Select("select g.*, mg.stock_count, mg.miaosha_price,mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.id = g.id where g.id=#{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count-1 where goods_id = #{goodsId}")
    public void reduceStock(MiaoshaGoods g);
}
