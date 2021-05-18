package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;
import com.wugui.docs.code.model.FieldModel;

public class JavaFieldBuilder implements ICodeBuilder {

    private String fieldTemplate;
    private FieldModel entryFieldModel;

    public JavaFieldBuilder(String fieldTemplate) {
        this.fieldTemplate = fieldTemplate;
    }

    public ICodeBuilder setEntryFieldModel(FieldModel entryFieldModel) {
        this.entryFieldModel = entryFieldModel;
        return this;
    }

    @Override
    public String build() {
        String template = fieldTemplate;
        template = template.replace("${FIELD_TYPE}",entryFieldModel.getFieldType());
        template = template.replace("${FIELD_NAME}",entryFieldModel.getFieldName());
        template = template.replace("${COMMENT}",entryFieldModel.getComment());
        return template + "\n";
    }
}
