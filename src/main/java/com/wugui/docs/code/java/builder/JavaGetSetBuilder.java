package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;
import com.wugui.docs.code.model.FieldModel;

/**
 * 字段Get/Set方法代码生成
 */
public class JavaGetSetBuilder implements ICodeBuilder {
    /** 代码模板 */
    private String codeTemplate;
    /** 字段对象 */
    private FieldModel fieldModel;

    public JavaGetSetBuilder(String codeTemplate) {
        this.codeTemplate = codeTemplate;
    }

    public ICodeBuilder setFieldModel(FieldModel entryFieldModel) {
        this.fieldModel = entryFieldModel;
        return this;
    }

    @Override
    public String build() {
        String template = this.codeTemplate;
        template = template.replace("${REMOTE_FIELD_NAME}", fieldModel.getRemoteFieldName());
        template = template.replace("${CASE_FIELD_NAME}", fieldModel.getCaseFieldName());
        template = template.replace("${FIELD_NAME}", fieldModel.getFieldName());
        template = template.replace("${FIELD_TYPE}", fieldModel.getFieldType());
        return template + "\n";
    }

}
