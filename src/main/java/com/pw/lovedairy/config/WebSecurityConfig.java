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
 * @author ?????????
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        //??????swagger
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
        //??????jwt?????????
        http.addFilterBefore(
                jwtAuthenticationTokenFilter,
                UsernamePasswordAuthenticationFilter.class);


        http
                .authorizeRequests()//????????????
                .requestMatchers( CorsUtils::isPreFlightRequest ).permitAll()
                .antMatchers( "/login.html").permitAll() //??????????????????
                //.antMatchers("/comment/teacher").hasAnyRole("student")
                //.antMatchers("/score/edit").hasAnyAuthority("socre:edit")
                .anyRequest().authenticated() //???????????????????????????????????????????????????
                //.and().exceptionHandling().accessDeniedPage("/noright.html") //??????????????????
                //???????????????????????????
                .and().exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        ResponseUtil.writeJSON(response, ResultData.fail("????????????????????????????????????????????????",null));
                    }
                })
                .and()
                .formLogin() //??????????????????
                .loginProcessingUrl("/user/login").permitAll()//????????????
                //.failureUrl("/fail.html")//???????????????????????????
                //.defaultSuccessUrl("/index.jhtml")//??????????????????????????????
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(
                            HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse,
                            Authentication authentication) throws IOException, ServletException {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        LoginUser loginUser = new LoginUser();
                        //??????jwt
                        Date now = new Date();
                        String token = JwtTokenUtil.getAccessToken(userDetails.getUsername(),now);
                        //???token??????????????????????????????
                        String refreshToken = JwtTokenUtil.getRefreshAccessToken(userDetails.getUsername(),now);
                        //???????????????????????????refreshToken
                        SecurityUserContext.tokenCache.put(userDetails.getUsername(),refreshToken);
                        //??????????????????token
                        loginUser.setToken(token);
                        ResponseUtil.writeJSON(httpServletResponse, ResultData.success(null,loginUser));
                    }
                })
                .failureHandler(new AuthenticationFailureHandler(){

                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        //?????????????????????
                        ResponseUtil.writeJSON(httpServletResponse,ResultData.fail("????????????",null));

                    }
                })
                .usernameParameter("username")
                .passwordParameter("password")
                //.loginPage("/login.jhtml").permitAll() //??????????????????
                .and()
                .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                            //???????????????????????????
                        ResponseUtil.writeJSON(httpServletResponse,new ResultData(50008,"???????????????????????????",null));

                    }
                })
                .and()
                .cors() //????????????
                .and()
                .logout().permitAll()
                .and().csrf().disable();
                ;//????????????

                 //.and().csrf().disable();//??????csrf
    }


    //??????DaoAuthenticationProvider???????????????????????????userDetailsService?????????provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userDetailsService) {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);

        //??????????????????
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

}

