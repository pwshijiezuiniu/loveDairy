package com.pw.lovedairy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author 张耀斌
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultData {
    Integer code;
    String message;
    Object data;

    public static ResultData success(String msg,Object data){
        ResultData rd = new ResultData();
        rd.setCode(20000);
        rd.setMessage(msg);
        rd.setData(data);
        return rd;
    }
    public static ResultData fail(String msg,Object data){
        ResultData rd = new ResultData();
        rd.setCode(40000);
        rd.setMessage(msg);
        rd.setData(data);
        return rd;
    }
}
