package com.wugui.docs.parser.annotation;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.wugui.docs.parser.ParamNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/28 15:32:41
 **/
public class ParamMappingParserFactory {

    private static Map<String, MappingParser> parserMap = new HashMap<>();

    static {
        // 无配置项
        parserMap.put(MarkerAnnotationExpr.class.getSimpleName(), new MarketParser());
        // 单个配置项
        parserMap.put(SingleMemberAnnotationExpr.class.getSimpleName(), new SingleMemberParser());
        // 多个配置项
        parserMap.put(NormalAnnotationExpr.class.getSimpleName(), new MultiMemberParser());
    }

    public static void parse(ParamNode node, AnnotationExpr expr) {
        MappingParser parser = parserMap.get(expr.getClass().getSimpleName());
        parser.parse(node, expr);
    }

    static class MarketParser implements MappingParser<ParamNode> {
        @Override
        public void parse(ParamNode node, AnnotationExpr expr) {
            node.setRequired(true);
        }
    }

    static class SingleMemberParser implements MappingParser<ParamNode> {
        @Override
        public void parse(ParamNode node, AnnotationExpr expr) {
            node.setName(((StringLiteralExpr) ((SingleMemberAnnotationExpr) expr).getMemberValue()).getValue());
        }
    }

    static class MultiMemberParser implements MappingParser<ParamNode> {
        @Override
        public void parse(ParamNode node, AnnotationExpr expr) {
            ((NormalAnnotationExpr) expr).getPairs().forEach(pair -> {
                String exprName = pair.getNameAsString();
                if ("required".equals(exprName)) {
                    Boolean exprValue = ((BooleanLiteralExpr) pair.getValue()).getValue();
                    node.setRequired(Boolean.valueOf(exprValue));
                } else if ("value".equals(exprName)) {
                    String exprValue = ((StringLiteralExpr) pair.getValue()).getValue();
                    node.setName(exprValue);
                }
            });
        }
    }
}
