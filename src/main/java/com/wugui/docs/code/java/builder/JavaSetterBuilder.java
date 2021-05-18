package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;
import com.wugui.docs.code.model.FieldModel;

public class JavaSetterBuilder implements ICodeBuilder {

    private String setterTemplate;
    private FieldModel entryFieldModel;

    public JavaSetterBuilder(String setterTemplate) {
        this.setterTemplate = setterTemplate;
    }

    public ICodeBuilder setEntryFieldModel(FieldModel entryFieldModel) {
        this.entryFieldModel = entryFieldModel;
        return this;
    }

    @Override
    public String build() {
        String template = this.setterTemplate;
        template = template.replace("${REMOTE_FIELD_NAME}",entryFieldModel.getRemoteFieldName());
        template = template.replace("${CASE_FIELD_NAME}",entryFieldModel.getCaseFieldName());
        template = template.replace("${FIELD_NAME}",entryFieldModel.getFieldName());
        template = template.replace("${FIELD_TYPE}",entryFieldModel.getFieldType());
        return template + "\n";
    }

}
