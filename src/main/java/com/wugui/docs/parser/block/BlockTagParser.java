package com.wugui.docs.parser.block;

import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/27 22:36:18
 **/
public interface BlockTagParser<T> {

    void putData(T node, JavadocBlockTag tag);
}
