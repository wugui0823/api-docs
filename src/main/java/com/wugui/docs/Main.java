package com.wugui.docs;

import com.wugui.docs.config.DocsConfig;
import com.wugui.docs.core.Docs;

/**
 * @author : zhangbo.chen
 * @since : 2021/5/12 14:16:18
 **/
public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        DocsConfig config = new DocsConfig();
//        config.setProjectPath("I:\\workspace\\idea_worspace\\personal\\api-docs\\src\\main\\java\\com\\wugui\\docs\\controller");
        config.addJavaSrcPath("I:\\workspace\\idea_worspace\\personal\\api-docs\\src\\main\\java\\com\\wugui\\docs\\controller");
        config.setProjectName("demo"); // 项目名称
        config.setVersion("V1.0");       // 声明该API的版本
        config.setDocsPath("K:\\api"); // 生成API 文档所在目录
        Docs.buildHtmlDocs(config); // 执行生成文档
        System.out.println("used time(ms) :" + (System.currentTimeMillis() - startTime));
    }
}
