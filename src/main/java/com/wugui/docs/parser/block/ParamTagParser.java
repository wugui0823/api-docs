package com.wugui.docs.parser.block;

import com.github.javaparser.javadoc.JavadocBlockTag;
import com.wugui.docs.parser.ParamNode;
import com.wugui.docs.parser.RequestNode;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/27 22:26:00
 **/
public class ParamTagParser implements BlockTagParser<RequestNode> {

    @Override
    public void putData(RequestNode requestNode, JavadocBlockTag blockTag) {
        requestNode.addParamNode(new ParamNode(blockTag.getName().orElse(null), blockTag.getContent().toText()));
    }
}
