package com.wugui.docs.code.provider;

import org.apache.commons.lang.StringUtils;

public class DocFieldHelper {

    public static String getPrefFieldName(String originFieldName){
        String[] names = originFieldName.split("_");
        if(names.length == 1){
            return StringUtils.uncapitalize(names[0]);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.uncapitalize(names[0]));
        for (int i = 1; i < names.length; i++) {
            builder.append(StringUtils.capitalize(names[i]));
        }
        return builder.toString();
    }

}
