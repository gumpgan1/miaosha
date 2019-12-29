package com.imooc.miaosha.config;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKE);
        String cookieToken = getCookieValue(request,MiaoshaUserService.COOKI_NAME_TOKE);
        if(StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)){
            return  null;
        }
       String token = StringUtils.isEmpty(paramToken) ? cookieToken:paramToken;
       MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(token,response);
       return miaoshaUser;
    }

    private String getCookieValue(HttpServletRequest request, String cookiNameToke) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length <=0)
            return null;;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookiNameToke)){
                return cookie.getValue();
            }
        }

        return null;
    }
}
