package com.wugui.docs.code.provider;

import com.wugui.docs.util.Resources;
import com.wugui.docs.util.Utils;

import java.io.IOException;

public class TemplateProvider {
	public static String provideForName(String templateName) throws IOException {
		return Utils.streamToString(Resources.getTemplateFile(templateName));
    }
}
