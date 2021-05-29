package com.wugui.docs.controller;

import com.sun.istack.internal.NotNull;
import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户模块
 * @author : zhangbo.chen
 * @since : 2021/5/17 9:08:18
 **/
@RestController(value = "user")
@RequestMapping(name="user", value = "user")
public class UserController {

    /**
     * 登陆
     * @param name
     * @param request
     * @param response
     * @return
     */
    @GetMapping("login")
    public UserInfo login(String name, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("hello2 " + name);
        return new UserInfo();
    }

    /**
     * 泛型返回用例
     * @param name 用户名
     * @param password  密码
     * @return
     */
    @RequestMapping(value = "login", headers = {}, method = {RequestMethod.GET})
    public Result<UserInfo> login(@NotNull String name, String password) {
        System.out.println("hello2 " + name);
        return ResultBuilder.success(new UserInfo());
    }
}
