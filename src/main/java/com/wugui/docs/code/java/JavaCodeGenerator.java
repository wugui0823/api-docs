package com.wugui.docs.code.java;

import com.wugui.docs.code.CodeGenerator;
import com.wugui.docs.code.java.builder.JavaClassBuilder;
import com.wugui.docs.code.java.builder.JavaFieldBuilder;
import com.wugui.docs.code.java.builder.JavaGetterBuilder;
import com.wugui.docs.code.java.builder.JavaSetterBuilder;
import com.wugui.docs.code.model.FieldModel;
import com.wugui.docs.code.provider.IFieldProvider;
import com.wugui.docs.code.provider.ProviderFactory;
import com.wugui.docs.code.provider.TemplateProvider;
import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.parser.ResponseNode;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.List;

public class JavaCodeGenerator extends CodeGenerator {

	private static final String FILE_FIELD_TEMPLATE = "Java_Entity_Field.tpl";
	private static final String FILE_GETTER_TEMPLATE = "Java_Entity_Getter.tpl";
	private static final String FILE_SETTER_TEMPLATE = "Java_Entity_Setter.tpl";
	private static final String FILE_CLASS_TEMPLATE = "Java_Entity.tpl";
	private static final String FILE_CODE_TEMPLATE = "Code_File.html.tpl";
	private static final String JAVA_CODE_DIR = "javaBean";

	private static String sFieldTemplate, sGetterTemplate, sSetterTemplate, sClassTemplate, sCodeTemplate;
	static{
		JavaTemplateProvider resourceTemplateProvider = new JavaTemplateProvider();
		try {
			sFieldTemplate = resourceTemplateProvider.provideTemplateForName(FILE_FIELD_TEMPLATE);
			sGetterTemplate = resourceTemplateProvider.provideTemplateForName(FILE_GETTER_TEMPLATE);
			sSetterTemplate = resourceTemplateProvider.provideTemplateForName(FILE_SETTER_TEMPLATE);
			sClassTemplate = resourceTemplateProvider.provideTemplateForName(FILE_CLASS_TEMPLATE);
			sCodeTemplate = TemplateProvider.provideTemplateForName(FILE_CODE_TEMPLATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JavaCodeGenerator(ResponseNode responseNode) {
		super(responseNode);
	}
	
	@Override
	public String generateNodeCode(ClassNode respNode) throws IOException {
		IFieldProvider entryProvider = ProviderFactory.createProvider();
		List<FieldModel> entryFields = entryProvider.provideFields(respNode);
		if(CollectionUtils.isEmpty(entryFields)){
			return "";
		}
		StringBuilder fieldStrings = new StringBuilder();
		StringBuilder methodStrings = new StringBuilder();

		JavaFieldBuilder fieldBuilder = new JavaFieldBuilder(sFieldTemplate);
		JavaGetterBuilder getterBuilder = new JavaGetterBuilder(sGetterTemplate);
		JavaSetterBuilder setterBuilder = new JavaSetterBuilder(sSetterTemplate);

		for (FieldModel field : entryFields) {
			fieldStrings.append(fieldBuilder.setEntryFieldModel(field).build());
			methodStrings.append(getterBuilder.setEntryFieldModel(field).build());
			methodStrings.append(setterBuilder.setEntryFieldModel(field).build());
		}
		if (methodStrings.charAt(methodStrings.length() - 1) == '\n') {
			methodStrings.deleteCharAt(methodStrings.length() - 1);
		}
		JavaClassBuilder classBuilder =
				new JavaClassBuilder(sClassTemplate, respNode.getClassName(),
						fieldStrings.toString(), methodStrings.toString());
		return classBuilder.build();
	}

	@Override
	public String getRelativeCodeDir() {
		return JAVA_CODE_DIR;
	}

	@Override
	public String getCodeTemplate() {
		return sCodeTemplate;
	}
}
