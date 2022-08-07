package com.pw.lovedairy.filter;


import com.pw.lovedairy.common.JwtTokenUtil;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityUserContext extends UserContext {
    //用于存储token信息
    public  static Map<String,String> tokenCache = new HashMap<>();

    @Override
    public String getCurrentAccount() {
        //获取Security上下文
        SecurityContext securityContext = SecurityContextHolder.getContext();
        //从其中获取用户的账号
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }


    public static String getJwtToken(String account){
        //生成jwt
        Date now = new Date();
        String token = JwtTokenUtil.getAccessToken(account,now);
        //将token也在服务器端进行保存
        String refreshToken = JwtTokenUtil.getRefreshAccessToken(account,now);
        //在服务器端存储的是refreshToken
        SecurityUserContext.tokenCache.put(account,refreshToken);
        return token;
    }
}
