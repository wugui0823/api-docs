package com.wugui.docs.parser.block;

import com.github.javaparser.javadoc.JavadocBlockTag;
import com.wugui.docs.parser.CommonNode;
import com.wugui.docs.parser.RequestNode;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/27 22:26:00
 **/
public class DescribeTagParser<T extends CommonNode> implements BlockTagParser<T> {

    @Override
    public void putData(T no, JavadocBlockTag blockTag) {
        no.setDescription(blockTag.getContent().toText());
    }
}
