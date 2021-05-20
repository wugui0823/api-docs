package com.wugui.docs.controller;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口统一返回规范
 * @author : zhangbo.chen
 * @since : 2021/5/18 20:45:59
 **/
@Getter
@Setter
public class Result<T> {
    /** 状态码 */
    private int code;
    /** 请求处理信息 */
    private String msg;
    /** 返回对象 */
    private T t;
}
