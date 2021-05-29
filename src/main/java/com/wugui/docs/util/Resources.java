package com.wugui.docs.util;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Resources {

    /**
     * 获取代码模板文件
     * @param fileName
     * @return
     */
    public static InputStream getTemplateFile(String fileName) {
        return Resources.class.getResourceAsStream("/" + fileName);
    }

    public static String getTemplateFilePath(String fileName) {
        return Resources.class.getResource("/" + fileName).getPath();
    }

    /**
     * 获取Freemarker模板文件
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Template getFreemarkerTemplate(String fileName) throws IOException {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_0);
        conf.setDefaultEncoding("utf-8");
        conf.setClassForTemplateLoading(Resources.class, "/");
        return conf.getTemplate(fileName);
    }
}
