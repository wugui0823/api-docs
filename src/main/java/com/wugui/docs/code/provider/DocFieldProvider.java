package com.wugui.docs.code.provider;

import com.wugui.docs.code.model.FieldModel;
import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.parser.FieldNode;
import com.wugui.docs.util.ParseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档生成字段需要的字段提供者
 */
public class DocFieldProvider implements FieldProvider {

	@Override
	public List<FieldModel> provideFields(ClassNode node) {
		List<FieldNode> fieldNodes = node.getChildNodes();
		if (CollectionUtils.isEmpty(fieldNodes)) {
			return null;
		}
		List<FieldModel> modelList = new ArrayList<>();
		FieldModel field;
		for (FieldNode recordNode : fieldNodes) {
			field = new FieldModel();
			String fieldName = DocFieldHelper.getPrefFieldName(recordNode.getName());
			field.setCaseFieldName(StringUtils.capitalize(fieldName));
			field.setFieldName(fieldName);
			field.setFieldType(ParseUtils.unifyType(recordNode.getType()));
			field.setRemoteFieldName(recordNode.getName());
			field.setComment(recordNode.getDescription());
			modelList.add(field);
		}
		return modelList;
	}
	
}
