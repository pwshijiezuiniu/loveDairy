package com.pw.lovedairy.filter;

import com.pw.lovedairy.entity.vo.LoginUser;


import java.util.ArrayList;

public abstract class UserContext {
    static ThreadLocal<LoginUser> loginUserHolder = new ThreadLocal<>();

    public static void holdLoginUser(LoginUser loginUser){
        loginUserHolder.set(loginUser);
    }
    public static LoginUser getCurrentUser(){

        ArrayList a;
        LoginUser loginUser = loginUserHolder.get();
        return loginUser;
    }

    public abstract String getCurrentAccount();
}
