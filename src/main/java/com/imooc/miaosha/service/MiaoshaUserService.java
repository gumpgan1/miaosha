package com.imooc.miaosha.service;


import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.PAData;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKE = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){

        return miaoshaUserDao.getById(id);
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo){
        if(loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String salt = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDbPass(password, salt);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(miaoshaUser,token,response);
        return true;

    }

    public MiaoshaUser getByToken(String token, HttpServletResponse response) {
        if(StringUtils.isEmpty(token)){
            return null;
        }

        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);

        //延长cookie有效期时间
        if(miaoshaUser!=null)
            addCookie(miaoshaUser,token,response);

        return miaoshaUser;

    }

    private void addCookie(MiaoshaUser miaoshaUser,String token, HttpServletResponse response){
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        Cookie cookie = new Cookie(COOKI_NAME_TOKE,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
