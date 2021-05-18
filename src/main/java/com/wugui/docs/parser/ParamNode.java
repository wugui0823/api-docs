package com.wugui.docs.parser;

public class ParamNode {

	private String name;
	private String type;
	private Boolean required = Boolean.FALSE;
	private String description;
	private Boolean jsonBody = Boolean.FALSE;

	public ParamNode() {
	}

	public ParamNode(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getJsonBody() {
		return jsonBody;
	}

	public void setJsonBody(Boolean jsonBody) {
		this.jsonBody = jsonBody;
	}
}
