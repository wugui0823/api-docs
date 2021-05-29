package com.wugui.docs.parser.block;

import com.github.javaparser.javadoc.JavadocBlockTag;
import com.wugui.docs.parser.CommonNode;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/27 22:26:00
 **/
public class AuthorTagParser<T extends CommonNode> implements BlockTagParser<T> {

    @Override
    public void putData(T node, JavadocBlockTag blockTag) {
        node.setAuthor(blockTag.getContent().toText());
    }
}
