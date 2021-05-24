package com.wugui.docs.core;

import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.LogUtils;
import com.wugui.docs.util.Resources;
import com.wugui.docs.util.Utils;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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
        } catch (TemplateException | IOException ex) {
            LogUtils.error("generate index fail !!!", ex);
        }
        copyCssStyle();
    }

    private void copyCssStyle() {
        try {
            String cssFileName = "style.css";
            File cssFile = new File(DocContext.getDocPath(), cssFileName);
            Utils.writeToDisk(cssFile, Utils.streamToString(Resources.getTemplateFile(cssFileName)));
        } catch (IOException e) {
            LogUtils.error("copyCssStyle fail", e);
        }

    }

    private Template getIndexTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-index.html.ftl");
    }
}
