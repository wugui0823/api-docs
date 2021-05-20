package com.wugui.docs.code;

import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;

/**
 * 代码生成抽象类
 */
public abstract class AbstractCodeGenerator<T extends ClassNode> implements CodeGenerator<T> {
	/** 要生成代码的对象 */
	private T classNode;
	/** 代码文件存放相对路径 */
	private String codeRelativePath;

	public AbstractCodeGenerator() {
		this.codeRelativePath = getRelativeCodeDir();
	}

	public AbstractCodeGenerator(T classNode){
		this.classNode = classNode;
		this.codeRelativePath = getRelativeCodeDir();
	}

	/**
	 * 生成代码
	 * @return 返回代码的相对目录
	 * @throws IOException
	 */
	public String generateCode() throws IOException {
		if (classNode == null && CollectionUtils.isEmpty(classNode.getChildNodes())) {
			return "";
		}
		String codeBody = generateClassCode(classNode);
		final String sCodeTemplate = getCodeTemplate();
		ICodeBuilder codeBuilder = new CodeFileBuilder(classNode.getClassName(), codeBody, sCodeTemplate);
		final String htmlPath = getFileName();
		File codePath = Utils.createFileIfAbsent(DocContext.getDocPath() + codeRelativePath);
		Utils.writeToDisk(new File(codePath, htmlPath), codeBuilder.build());
		return String.format("%s/%s", codeRelativePath, htmlPath);
	}

	private String generateClassCode(ClassNode rootNode) {
		if (rootNode == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(generateNodeCode(rootNode) + '\n');
		// 判断对象是否还有属性
		rootNode.getChildNodes().forEach(e -> builder.append(generateClassCode(e.getChildNode())));
		return builder.toString();
	}

	/***
	 * 产生单个ResponseNode节点的Code
	 * @param classNode
	 * @return
	 */
	public abstract String generateNodeCode(ClassNode classNode);

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

	/**
	 * 获取文件名称
	 * @return
	 */
	public abstract String getFileName();

	public T getClassNode() {
		return classNode;
	}

	public CodeGenerator setClassNode(T classNode) {
		this.classNode = classNode;
		return this;
	}
}
