package com.wugui.docs.code.provider;

import com.wugui.docs.code.model.FieldModel;
import com.wugui.docs.parser.ClassNode;

import java.util.List;

public interface IFieldProvider {
	/**
	 * 返回页面渲染需要的字段集合
	 * @param respNode
	 * @return
	 */
	List<FieldModel> provideFields(ClassNode respNode);
}
