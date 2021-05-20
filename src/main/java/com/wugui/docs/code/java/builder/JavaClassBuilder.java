package com.wugui.docs.code.java.builder;

import com.wugui.docs.code.ICodeBuilder;

public class JavaClassBuilder implements ICodeBuilder {

    private String className;
    private String context;
    private String classTemplate;

    public JavaClassBuilder(String classTemplate, String className, String context) {
        this.className = className;
        this.context = context;
        this.classTemplate = classTemplate;
    }

    @Override
    public String build() {
        classTemplate = classTemplate.replace("${CLASS_NAME}",className);
        classTemplate = classTemplate.replace("${CONTENT}",context);
        return classTemplate;
    }
}
