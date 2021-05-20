package com.wugui.docs.code;

import com.wugui.docs.parser.ClassNode;

import java.io.IOException;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/18 19:54:23
 **/
public interface CodeGenerator<T extends ClassNode> {

    CodeGenerator setClassNode(T node);

    T getClassNode();

    /**
     * 生成代码
     * @return 返回代码的相对目录
     * @throws IOException
     */
    String generateCode() throws IOException;
}
