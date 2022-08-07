package com.pw.lovedairy.common;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 张耀斌
 */
public class ResponseUtil {

    public static void writeJSON(HttpServletResponse response, Object obj){
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        try {
            response.getWriter().write(JSONObject.toJSONString(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
