package com.pw.lovedairy.serivce;


import com.pw.lovedairy.dao.UserDao;
import com.pw.lovedairy.entity.RightEntity;
import com.pw.lovedairy.entity.RoleEntity;
import com.pw.lovedairy.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 张耀斌
 */
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    public String getNickName(String account){
        return userDao.findByAccount(account).getNickName();
    }
    //重写springSecurity的登录机制
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //根据账号找到用户对象
        System.out.println(s+",,,,::::::::::::dsadsdawsdsd");
        UserEntity loginUser = userDao.findByAccount(s);
        System.out.println(loginUser);

        if(loginUser!=null){
            System.out.println(loginUser.getAccount()+","+loginUser.getPassword());
            UserDetails userDetails = new UserDetails() {
                //用户的权限
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
                    for (RoleEntity role : loginUser.getRoles()){
                        for(RightEntity right : role.getRights()){
                            //将用户的权限进行保存
                            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(right.getRightCode());
                            grantedAuthorityList.add(grantedAuthority);
                        }
                    }
                    return grantedAuthorityList;
                }

                //返回用户的密码
                @Override
                public String getPassword() {
                    return loginUser.getPassword();
                }

                //返回用户账号
                @Override
                public String getUsername() {
                    return loginUser.getAccount();
                }

                //当前用户账号是否没有过期
                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                //当前用户是否没有锁定
                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                //当前用户的密码是否没有过期
                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }

                //当前用户是否可用
                @Override
                public boolean isEnabled() {
                    return true;
                }
            };
            //返回登录成功的用户对象
            return userDetails;
        }
        return null;
    }
}
