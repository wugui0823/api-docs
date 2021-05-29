package com.wugui.docs.parser.annotation;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.wugui.docs.parser.ControllerNode;
import org.apache.commons.lang.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/28 15:32:41
 **/
public class ClassMappingParserFactory {

    private static Map<String, MappingParser> parserMap = new HashMap<>();

    static {
        // 无配置项
        parserMap.put(MarkerAnnotationExpr.class.getSimpleName(), new MarketParser());
        // 单个配置项
        parserMap.put(SingleMemberAnnotationExpr.class.getSimpleName(), new SingleMemberParser());
        // 多个配置项
        parserMap.put(NormalAnnotationExpr.class.getSimpleName(), new MultiMemberParser());
    }

    public static void parse(ControllerNode node, AnnotationExpr expr) {
        MappingParser parser = parserMap.get(expr.getClass().getSimpleName());
        parser.parse(node, expr);
    }

    static class MarketParser implements MappingParser<ControllerNode> {
        @Override
        public void parse(ControllerNode node, AnnotationExpr expr) {
        }
    }

    static class SingleMemberParser implements MappingParser<ControllerNode> {
        @Override
        public void parse(ControllerNode node, AnnotationExpr expr) {
            node.setBaseUrl(((SingleMemberAnnotationExpr) expr).getMemberValue().toString());
        }
    }

    static class MultiMemberParser implements MappingParser<ControllerNode> {
        @Override
        public void parse(ControllerNode node, AnnotationExpr expr) {
            ((NormalAnnotationExpr) expr).getPairs().stream()
                    .filter(v -> ArrayUtils.contains(new String[]{"path", "value"}, v.getNameAsString()))
                    .findFirst()
                    .ifPresent(p -> {
                        node.setBaseUrl(p.getValue().toString());
                    });
        }
    }
}
