package com.wugui.docs.code.java;

import com.wugui.docs.code.AbstractCodeGenerator;
import com.wugui.docs.code.java.builder.JavaClassBuilder;
import com.wugui.docs.code.java.builder.JavaFieldBuilder;
import com.wugui.docs.code.java.builder.JavaGetSetBuilder;
import com.wugui.docs.code.model.FieldModel;
import com.wugui.docs.code.provider.DocFieldProvider;
import com.wugui.docs.code.provider.FieldProvider;
import com.wugui.docs.code.provider.TemplateProvider;
import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.parser.ResponseNode;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * 接口返回参数对象代码生成器
 */
public class JavaBeanGenerator extends AbstractCodeGenerator<ResponseNode> {

	private static final String FILE_CLASS_TEMPLATE = "Java_Entity.tpl";
	private static final String FILE_FIELD_TEMPLATE = "Java_Entity_Field.tpl";
	private static final String FILE_GETSET_TEMPLATE = "Java_Entity_GetSet.tpl";
	private static final String FILE_CODE_TEMPLATE = "Code_File.html.tpl";
	private static final String JAVA_CODE_DIR = "javaBean";

	private static String sFieldTemplate, sGetSetTemplate, sClassTemplate, sCodeTemplate;
	private FieldProvider fieldProvider = new DocFieldProvider();

	static{
		try {
			sFieldTemplate = TemplateProvider.provideForName(FILE_FIELD_TEMPLATE);
			sGetSetTemplate = TemplateProvider.provideForName(FILE_GETSET_TEMPLATE);
			sClassTemplate = TemplateProvider.provideForName(FILE_CLASS_TEMPLATE);
			sCodeTemplate = TemplateProvider.provideForName(FILE_CODE_TEMPLATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String generateNodeCode(ClassNode respNode) {
		List<FieldModel> fields = fieldProvider.provideFields(respNode);
		if(CollectionUtils.isEmpty(fields)){
			return "";
		}
		StringBuilder fieldStrings = new StringBuilder();
		StringBuilder methodStrings = new StringBuilder();

		JavaFieldBuilder fieldBuilder = new JavaFieldBuilder(sFieldTemplate);
		JavaGetSetBuilder setterBuilder = new JavaGetSetBuilder(sGetSetTemplate);

		for (FieldModel field : fields) {
			fieldStrings.append(fieldBuilder.setFieldModel(field).build());
			methodStrings.append(setterBuilder.setFieldModel(field).build());
		}
		if (methodStrings.charAt(methodStrings.length() - 1) == '\n') {
			methodStrings.deleteCharAt(methodStrings.length() - 1);
		}
		String content = fieldStrings.append(methodStrings).toString();
		JavaClassBuilder classBuilder =
				new JavaClassBuilder(sClassTemplate, respNode.getClassName(), content);
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

	@Override
	public String getFileName() {
		final String javaFileName = String.format("%s_%s_%s_%s.html",
				getClassNode().getRequestNode().getControllerNode().getPackageName().replace(".", "_"),
				getClassNode().getRequestNode().getControllerNode().getClassName(),
				getClassNode().getRequestNode().getMethodName(),
				getClassNode().getClassName());
		return javaFileName;
	}
}
