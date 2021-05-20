package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;
import com.wugui.docs.code.model.FieldModel;

/**
 * Java属性代码生成
 */
public class JavaFieldBuilder implements ICodeBuilder {
    /** 代码模板 */
    private String codeTemplate;
    /** 字段对象 */
    private FieldModel fieldModel;

    public JavaFieldBuilder(String codeTemplate) {
        this.codeTemplate = codeTemplate;
    }

    public ICodeBuilder setFieldModel(FieldModel entryFieldModel) {
        this.fieldModel = entryFieldModel;
        return this;
    }

    @Override
    public String build() {
        String template = codeTemplate;
        template = template.replace("${FIELD_TYPE}", fieldModel.getFieldType());
        template = template.replace("${FIELD_NAME}", fieldModel.getFieldName());
        template = template.replace("${COMMENT}", fieldModel.getComment());
        return template + "\n";
    }
}
