package com.wugui.docs.code.model;

/**
 * 对象字段信息
 */
public class FieldModel {

    private String remoteFieldName;
    private String caseFieldName;
    private String fieldName;
    private String fieldType;
    private String comment;
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getRemoteFieldName() {
        return remoteFieldName;
    }

    public void setRemoteFieldName(String remoteFieldName) {
        this.remoteFieldName = remoteFieldName;
    }

    public String getCaseFieldName() {
        return caseFieldName;
    }

    public void setCaseFieldName(String caseFieldName) {
        this.caseFieldName = caseFieldName;
    }

	public String getComment() {
		return comment == null ? "" : comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
