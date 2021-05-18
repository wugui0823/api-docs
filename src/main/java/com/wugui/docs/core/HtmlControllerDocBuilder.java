package com.wugui.docs.core;

import com.wugui.docs.code.CodeGenerator;
import com.wugui.docs.code.java.JavaCodeGenerator;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.RequestNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.Resources;
import com.wugui.docs.util.Utils;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HtmlControllerDocBuilder implements IControllerDocBuilder {

    @Override
    public String buildDoc(ControllerNode controllerNode) throws IOException {
        for (RequestNode requestNode : controllerNode.getRequestNodes()) {
            if (requestNode.getResponseNode() != null
                    && CollectionUtils.isNotEmpty(requestNode.getResponseNode().getChildNodes())) {
                CodeGenerator javaCodeGenerator = new JavaCodeGenerator(requestNode.getResponseNode());
                final String javaSrcUrl = javaCodeGenerator.generateCode();
                requestNode.setAndroidCodePath(javaSrcUrl);
            }
        }
        final Template ctrlTemplate = getControllerTpl();
        final File docFile = new File(DocContext.getDocPath(), controllerNode.getDocFileName());
        FileWriter docFileWriter = new FileWriter(docFile);
        Map<String, Object> data = new HashMap<>();
        data.put("controllerNodeList", DocContext.getControllerNodeList());
        data.put("controller", controllerNode);
        data.put("currentApiVersion", DocContext.getCurrentApiVersion());
        data.put("apiVersionList", DocContext.getApiVersionList());
        data.put("projectName", DocContext.getDocsConfig().getProjectName());
        data.put("i18n", DocContext.getI18n());

        try {
            ctrlTemplate.process(data, docFileWriter);
        } catch (TemplateException ex) {
            ex.printStackTrace();
        } finally {
            Utils.closeSilently(docFileWriter);
        }
        return Utils.streamToString(new FileInputStream(docFile));
    }

    private Template getControllerTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-controller.html.ftl");
    }

}
