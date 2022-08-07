package com.pw.lovedairy.controller;

import com.pw.lovedairy.common.ResultData;
import com.pw.lovedairy.entity.UserDairy;
import com.pw.lovedairy.serivce.DairyService;
import com.pw.lovedairy.serivce.UserService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    DairyService dairyService;
    @PostMapping("login")
    public void login(String username,String password){}

    @GetMapping("info")
    public ResultData getInfo(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String,String> map =  new HashMap<>();
        map.put("nickName", userService.getNickName(userDetails.getUsername()));
        return ResultData.success(null,map);
    }
    @PostMapping("logout")
    public ResultData logout(){
        return ResultData.success(null,null);
    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @PostMapping("edit")
    public ResultData submit(String account,String text,String title){
        UserDairy userDairy = new UserDairy();
        userDairy.setTitle(title);
        userDairy.setAccount(account);
        userDairy.setText(text);
        userDairy.setTime(new Date());
        dairyService.save(userDairy);

        return ResultData.success("提交成功",null);
    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @DeleteMapping("delete")
    public ResultData delete(Integer dairyId){
        dairyService.delete(dairyId);
        return ResultData.success(null,null);
    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @PutMapping("alter")
    public ResultData alter(Integer dairyId,String text){
        dairyService.alter(dairyId,text);
        return ResultData.success(null,null);
    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @PostMapping("commit")
    public ResultData alter(String title,String text){
        UserDairy userDairy = new UserDairy();
        userDairy.setTime(new Date());
        userDairy.setText(text);
        userDairy.setTitle(title);
        userDairy.setAccount(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        dairyService.save(userDairy);
        return ResultData.success(null,dairyService.findId(userDairy));
    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @PostMapping("getSelf")
    public ResultData getSelf(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String account = userDetails.getUsername();
        return ResultData.success(null,dairyService.findAllByAccount(account));

    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @PostMapping("getOthers")
    public ResultData getOthers(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String account = userDetails.getUsername();
        return ResultData.success(null,dairyService.findAllByLoverAccount(account));

    }
    @PreAuthorize("hasAuthority('user:editdairy')")
    @GetMapping("getSingle")
    public ResultData getSingle(Integer dairyId){
        return ResultData.success(null,dairyService.findById(dairyId));
    }
}
