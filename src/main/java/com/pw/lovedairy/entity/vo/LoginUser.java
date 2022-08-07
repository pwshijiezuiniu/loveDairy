package com.pw.lovedairy.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户身份权限
 * 
 * @author xyao
 */
@Data
public class LoginUser implements Serializable {

    private Integer userId;


    private String account;
    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户名昵称
     */
    private String nickName;
    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 权限列表
     */
    private Set<String> permissions;

}
