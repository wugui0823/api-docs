package com.wugui.docs.parser.annotation;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.HeaderNode;
import com.wugui.docs.parser.RequestNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/28 15:32:41
 **/
public class MethodMappingParserFactory {

    private static Map<String, MappingParser> parserMap = new HashMap<>();

    static {
        // 无配置项
        parserMap.put(MarkerAnnotationExpr.class.getSimpleName(), new MarketParser());
        // 单个配置项
        parserMap.put(SingleMemberAnnotationExpr.class.getSimpleName(), new SingleMemberParser());
        // 多个配置项
        parserMap.put(NormalAnnotationExpr.class.getSimpleName(), new MultiMemberParser());
    }

    public static void parse(RequestNode node, AnnotationExpr expr) {
        MappingParser parser = parserMap.get(expr.getClass().getSimpleName());
        parser.parse(node, expr);
    }

    static class MarketParser implements MappingParser<ControllerNode> {
        @Override
        public void parse(ControllerNode node, AnnotationExpr expr) {
        }
    }

    static class SingleMemberParser implements MappingParser<RequestNode> {
        @Override
        public void parse(RequestNode node, AnnotationExpr expr) {
            String url = ((SingleMemberAnnotationExpr) expr).getMemberValue().toString();
            node.setUrl("/" + url);
        }
    }

    static class MultiMemberParser implements MappingParser<RequestNode> {
        @Override
        public void parse(RequestNode node, AnnotationExpr expr) {
            ((NormalAnnotationExpr) expr).getPairs().forEach(p -> {
                String key = p.getNameAsString();
                if (StringUtils.equals("path", key) || StringUtils.equals("value", key)) {
                    node.setUrl("/" + p.getValue().toString());
                } else if (StringUtils.equals("headers", key)) {
                    Expression methodAttr = p.getValue();
                    if (methodAttr instanceof ArrayInitializerExpr) {
                        for (Node n : ((ArrayInitializerExpr) methodAttr).getValues()) {
                            String[] h = n.toString().split("=");
                            node.addHeaderNode(new HeaderNode(h[0], h[1]));
                        }
                    } else {
                        String[] h = p.getValue().toString().split("=");
                        node.addHeaderNode(new HeaderNode(h[0], h[1]));
                    }
                } else if (StringUtils.equals("method", key)) {
                    Expression methodAttr = p.getValue();
                    if (methodAttr instanceof ArrayInitializerExpr) {
                        for (Node n : ((ArrayInitializerExpr) methodAttr).getValues()) {
                            String[] split = n.toString().split("\\.");
                            node.addMethod(RequestMethod.valueOf(split[split.length - 1]).name());
                        }
                    } else {
                        String[] split = p.getValue().toString().split("\\.");
                        node.addMethod(RequestMethod.valueOf(split[split.length - 1]).name());
                    }
                }
            });
        }
    }
}
