package com.wugui.docs.code.java;

import com.wugui.docs.code.provider.TemplateProvider;

import java.io.IOException;

/**
 * Created by Darcy https://yedaxia.github.io/
 */
public class JavaTemplateProvider {

    public String provideTemplateForName(String templateName) throws IOException {
    	return TemplateProvider.provideTemplateForName(templateName);
    }
    
}
