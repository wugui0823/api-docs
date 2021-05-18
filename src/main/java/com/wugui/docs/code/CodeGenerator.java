package com.wugui.docs.code;

import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.parser.FieldNode;
import com.wugui.docs.parser.ResponseNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;

public abstract class CodeGenerator {

	private ResponseNode responseNode;
	private File codePath;
	private String codeRelativePath;

	public CodeGenerator(ResponseNode responseNode){
		this.responseNode = responseNode;
		this.codeRelativePath = getRelativeCodeDir();
		this.codePath = Utils.createFileIfAbsent(DocContext.getDocPath() + codeRelativePath);
	}

	/**
	 * 生成代码
	 * @return 返回代码的相对目录
	 * @throws IOException
	 */
	public String generateCode() throws IOException{
		if (CollectionUtils.isEmpty(responseNode.getChildNodes())) {
			return "";
		}
		StringBuilder codeBodyBuilder = new StringBuilder();
		generateCodeForBuilder(responseNode, codeBodyBuilder);
		final String sCodeTemplate = getCodeTemplate();
		ICodeBuilder codeBuilder = new CodeFileBuilder(responseNode.getClassName(), codeBodyBuilder.toString(), sCodeTemplate);
		final String javaFileName = String.format("%s_%s_%s_%s.html",
				responseNode.getRequestNode().getControllerNode().getPackageName().replace(".","_"),
				responseNode.getRequestNode().getControllerNode().getClassName(),
				responseNode.getRequestNode().getMethodName(), responseNode.getClassName());
		Utils.writeToDisk(new File(codePath, javaFileName), codeBuilder.build());
		return String.format("%s/%s", codeRelativePath, javaFileName);
	}

	private void generateCodeForBuilder(ClassNode rootNode, StringBuilder codeBodyBuilder) throws IOException {
		codeBodyBuilder.append(generateNodeCode(rootNode));
		codeBodyBuilder.append('\n');
		for (FieldNode recordNode : rootNode.getChildNodes()) {
			if (recordNode.getChildNode() != null) {
				generateCodeForBuilder(recordNode.getChildNode(), codeBodyBuilder);
			}
		}
	}

	/***
	 * 产生单个ResponseNode节点的Code
	 * @param classNode
	 * @return
	 * @throws IOException
	 */
	public abstract String generateNodeCode(ClassNode classNode) throws IOException;

	/**
	 * 获取代码的写入的相对目录
	 * @return
	 */
	public abstract String getRelativeCodeDir();

	/**
	 * 获取最终的代码模板
	 * @return
	 */
	public abstract String getCodeTemplate();
}
