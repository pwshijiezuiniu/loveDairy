package com.pw.lovedairy.config;


import com.pw.lovedairy.common.JwtTokenUtil;
import com.pw.lovedairy.common.ResponseUtil;
import com.pw.lovedairy.common.ResultData;
import com.pw.lovedairy.entity.vo.LoginUser;
import com.pw.lovedairy.filter.JwtAuthenticationTokenFilter;
import com.pw.lovedairy.filter.SecurityUserContext;
import com.pw.lovedairy.serivce.UserService;
import io.swagger.models.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 张耀斌
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行swagger
        web.ignoring().antMatchers(HttpMethod.GET.toString(),
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html/**",
                "/doc.html/**",
                "/webjars/**");
    }
    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //设置jwt过滤器
        http.addFilterBefore(
                jwtAuthenticationTokenFilter,
                UsernamePasswordAuthenticationFilter.class);


        http
                .authorizeRequests()//授权配置
                .requestMatchers( CorsUtils::isPreFlightRequest ).permitAll()
                .antMatchers( "/login.html").permitAll() //可以直接通过
                //.antMatchers("/comment/teacher").hasAnyRole("student")
                //.antMatchers("/score/edit").hasAnyAuthority("socre:edit")
                .anyRequest().authenticated() //其他没有配置的权限需要登录权限验证
                //.and().exceptionHandling().accessDeniedPage("/noright.html") //无权限的路径
                //处理没有权限的异常
                .and().exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        ResponseUtil.writeJSON(response, ResultData.fail("用户没有操作的权限，请联系管理员",null));
                    }
                })
                .and()
                .formLogin() //登录认证配置
                .loginProcessingUrl("/user/login").permitAll()//登陆请求
                //.failureUrl("/fail.html")//登录失败的访问页面
                //.defaultSuccessUrl("/index.jhtml")//登录成功之后访问页面
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(
                            HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse,
                            Authentication authentication) throws IOException, ServletException {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        LoginUser loginUser = new LoginUser();
                        //生成jwt
                        Date now = new Date();
                        String token = JwtTokenUtil.getAccessToken(userDetails.getUsername(),now);
                        //将token也在服务器端进行保存
                        String refreshToken = JwtTokenUtil.getRefreshAccessToken(userDetails.getUsername(),now);
                        //在服务器端存储的是refreshToken
                        SecurityUserContext.tokenCache.put(userDetails.getUsername(),refreshToken);
                        //登录用户保存token
                        loginUser.setToken(token);
                        ResponseUtil.writeJSON(httpServletResponse, ResultData.success(null,loginUser));
                    }
                })
                .failureHandler(new AuthenticationFailureHandler(){

                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        //登录失败的处理
                        ResponseUtil.writeJSON(httpServletResponse,ResultData.fail("登录失败",null));

                    }
                })
                .usernameParameter("username")
                .passwordParameter("password")
                //.loginPage("/login.jhtml").permitAll() //登陆页面地址
                .and()
                .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                            //没有登录状态的处理
                        ResponseUtil.writeJSON(httpServletResponse,new ResultData(50008,"没有登录，请先登录",null));

                    }
                })
                .and()
                .cors() //允许跨域
                .and()
                .logout().permitAll()
                .and().csrf().disable();
                ;//退出功能

                 //.and().csrf().disable();//禁用csrf
    }


    //配置DaoAuthenticationProvider，并且将自定义实现userDetailsService注入到provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userDetailsService) {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);

        //设置密码编码
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

}

