package com.wugui.docs.controller;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/18 20:48:48
 **/
public class ResultBuilder {

    public static Result success(Object o) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg("请求成功");
        result.setT(o);
        return result;
    }
}
