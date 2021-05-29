package com.wugui.docs.parser.annotation;

import com.github.javaparser.ast.expr.AnnotationExpr;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/28 15:32:41
 **/
public interface MappingParser<T> {

    void parse(T node, AnnotationExpr expr);
}
