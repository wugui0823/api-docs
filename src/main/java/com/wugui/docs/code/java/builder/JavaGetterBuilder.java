package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;
import com.wugui.docs.code.model.FieldModel;

public class JavaGetterBuilder implements ICodeBuilder {

    private String getterTemplate;
    private FieldModel entryFieldModel;

    public JavaGetterBuilder(String getterTemplate) {
        this.getterTemplate = getterTemplate;
    }

    public ICodeBuilder setEntryFieldModel(FieldModel entryFieldModel) {
        this.entryFieldModel = entryFieldModel;
        return this;
    }

    @Override
    public String build() {
        String template = getterTemplate;
        template = template.replace("${CASE_FIELD_NAME}",entryFieldModel.getCaseFieldName());
        template = template.replace("${FIELD_NAME}",entryFieldModel.getFieldName());
        template = template.replace("${FIELD_TYPE}",entryFieldModel.getFieldType());
        template = template.replace("${REMOTE_FIELD_NAME}",entryFieldModel.getRemoteFieldName());
        return template + "\n";
    }

}
