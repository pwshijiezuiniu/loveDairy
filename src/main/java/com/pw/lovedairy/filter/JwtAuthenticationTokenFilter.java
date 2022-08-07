package com.pw.lovedairy.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.pw.lovedairy.common.JwtTokenUtil;
import com.pw.lovedairy.serivce.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author 张耀斌
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userDetailsService;
    /**
     * 防止filter被执行两次
     */
    private static final String FILTER_APPLIED = "__spring_security_JwtAuthenPreFilter_filterApplied";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //防止过滤器被访问两次
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, true);
        //从请求头中获取jwt
        String token = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        if (!StringUtils.isEmpty(token)) {
            //获取账号凭证
            String username = JwtTokenUtil.getSubject(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                try {
                    if (JwtTokenUtil.verify(token, userDetails.getUsername())) {
                        //判断请求accessToken和服务器端的token的创建时间是否一致
                        String refreshToken = SecurityUserContext.tokenCache.get(userDetails.getUsername());
                        //还需要判断accessToken的创建时间是否和freshToken的创建时间一致
                        String accessTokenCreatedTime = JwtTokenUtil.getCreateTime(token);
                        String freshTokenCreatedTime = JwtTokenUtil.getCreateTime(refreshToken);
                        //如果accessToken和freshToken的创建时间相同才可以刷新
                        if (accessTokenCreatedTime.equals(freshTokenCreatedTime))
                        {
                            //保存用户状态
                            holderUser(request, userDetails);
                        }else{
                            throw new RuntimeException("用户在其他的地方登录");
                        }

                    }
                } catch (TokenExpiredException e) {
                    //只捕获过期的异常
                    //先判断refreshToken是否过期

                    //需要判断是否可以创建新的token，还是判定token无效
                    String refreshToken = SecurityUserContext.tokenCache.get(userDetails.getUsername());
                    if (refreshToken != null) {
                        //没有过期
                        if (!JwtTokenUtil.isExpired(refreshToken)) {
                            //还需要判断
                            String accessTokenCreatedTime = JwtTokenUtil.getCreateTime(token);
                            String freshTokenCreatedTime = JwtTokenUtil.getCreateTime(refreshToken);
                            //如果accessToken和freshToken的创建时间相同才可以刷新
                            if (accessTokenCreatedTime.equals(freshTokenCreatedTime)) {
                                Date now = new Date();
                                //创建一个新的token
                                String newToken = JwtTokenUtil.getAccessToken(userDetails.getUsername(), now);
                                //在头部中添加新的token
                                response.addHeader("authorization", newToken);
                                //保存用户状态
                                holderUser(request, userDetails);
                                //需要将服务器中的token换成新的token
                                String newFreshToken = JwtTokenUtil.getRefreshAccessToken(userDetails.getUsername(), now);
                                SecurityUserContext.tokenCache.put(userDetails.getUsername(), newFreshToken);
                                chain.doFilter(request, response);
                                return;
                            }

                        }

                    }
                    throw e;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void holderUser(HttpServletRequest request, UserDetails userDetails) {
        // 将用户信息存入 authentication，方便后续校验
        // 创建带权限的token直接会定义成登陆成功
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}