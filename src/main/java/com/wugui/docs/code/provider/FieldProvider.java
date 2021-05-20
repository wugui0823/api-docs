package com.wugui.docs.code.provider;

import com.wugui.docs.code.model.FieldModel;
import com.wugui.docs.parser.ClassNode;

import java.util.List;

/**
 * 文档渲染字段提供者
 */
public interface FieldProvider {
    /**
     * 返回页面渲染需要的字段集合
     *
     * @param node
     * @return
     */
    List<FieldModel> provideFields(ClassNode node);
}
