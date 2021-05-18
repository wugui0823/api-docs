package com.wugui.docs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试模块
 * @author : zhangbo.chen
 * @since : 2021/5/17 9:08:18
 **/
@RestController
@RequestMapping("test")
public class TestController {

    /**
     * 向xx打招呼
     * @param name 用户名称
     * @return
     */
    @GetMapping("hello")
    public String hello(String name) {
        return "hello:" + name;
    }

    /**
     * hello2 xxx
     * @param name
     * @return
     */
    @GetMapping("hello2")
    public void hello2(String name) {
        System.out.println("hello2 " + name);
    }

    /**
     * 登陆
     * @param name
     * @return
     */
    @GetMapping("login")
    public UserInfo login(String name) {
        System.out.println("hello2 " + name);
        return new UserInfo();
    }
}
