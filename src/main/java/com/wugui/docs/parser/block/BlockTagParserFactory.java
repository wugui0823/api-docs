package com.wugui.docs.parser.block;

import com.github.javaparser.javadoc.JavadocBlockTag;
import com.wugui.docs.consts.ClassTagConstants;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.RequestNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/27 20:56:05
 **/
public class BlockTagParserFactory {

    private static Map<String, BlockTagParser> methodParserMap = new HashMap<>();
    private static Map<String, BlockTagParser> classParserMap = new HashMap<>();

    static {
        methodParserMap.put(ClassTagConstants.PARAM, new ParamTagParser());
        methodParserMap.put(ClassTagConstants.AUTHOR, new AuthorTagParser<RequestNode>());
        methodParserMap.put(ClassTagConstants.DESCRIBE, new DescribeTagParser<RequestNode>());

        classParserMap.put(ClassTagConstants.AUTHOR, new AuthorTagParser<ControllerNode>());
        classParserMap.put(ClassTagConstants.DESCRIBE, new DescribeTagParser<ControllerNode>());
    }

    public static void putData(RequestNode node, JavadocBlockTag tag) {
        String tagName = tag.getTagName();
        BlockTagParser parser = methodParserMap.get(tagName);
        if (parser != null) {
            parser.putData(node, tag);
        }
    }

    public static void putData(ControllerNode node, JavadocBlockTag tag) {
        String tagName = tag.getTagName();
        BlockTagParser parser = classParserMap.get(tagName);
        if (parser != null) {
            parser.putData(node, tag);
        }
    }
}
