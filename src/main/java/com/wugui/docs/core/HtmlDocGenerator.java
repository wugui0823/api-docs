package com.wugui.docs.core;

import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.LogUtils;
import com.wugui.docs.util.Resources;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlDocGenerator extends AbsDocGenerator {

    public HtmlDocGenerator() {
        super(DocContext.controllerParser(), new HtmlControllerDocBuilder());
    }

    @Override
    void generateIndex(List<ControllerNode> controllerNodeList) {
        LogUtils.info("generate index start !!!");
        final File docFile = new File(DocContext.getDocPath(), "index.html");
        try (FileWriter docFileWriter = new FileWriter(docFile)) {
            final Template ctrlTemplate = getIndexTpl();
            Map<String, Object> data = new HashMap<>();
            data.put("controllerNodeList", controllerNodeList);
            data.put("currentApiVersion", DocContext.getCurrentVersion());
            data.put("apiVersionList", DocContext.getVersionList());
            data.put("projectName", DocContext.getDocsConfig().getProjectName());
            ctrlTemplate.process(data, docFileWriter);
            LogUtils.info("generate index done !!!");
            FileUtils.copyFileToDirectory(new File(Resources.getTemplateFilePath("style.css")), DocContext.getDocPathFile());
        } catch (Exception e) {
            LogUtils.error("generate index fail !!!", e);
        }
    }

    private Template getIndexTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-index.html.ftl");
    }
}
