package com.wugui.docs.core;

import com.wugui.docs.code.CodeGenerator;
import com.wugui.docs.code.java.JavaBeanGenerator;
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
    private CodeGenerator generator = new JavaBeanGenerator();
    @Override
    public String buildDoc(ControllerNode controllerNode) throws IOException {
        for (RequestNode reqNode : controllerNode.getRequestNodes()) {
            // 如果返回对象的属性，还存在属性，则生成对象文档
            if (reqNode.getResponseNode() != null
                    && CollectionUtils.isNotEmpty(reqNode.getResponseNode().getChildNodes())) {
                String javaSrcUrl = generator.setClassNode(reqNode.getResponseNode()).generateCode();
                reqNode.setAndroidCodePath(javaSrcUrl);
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("controllerNodeList", DocContext.getControllerNodeList());
        data.put("controller", controllerNode);
        data.put("currentApiVersion", DocContext.getCurrentVersion());
        data.put("apiVersionList", DocContext.getVersionList());
        data.put("projectName", DocContext.getDocsConfig().getProjectName());

        final File docFile = new File(DocContext.getDocPath(), controllerNode.getDocFileName());
        try (FileWriter docFileWriter = new FileWriter(docFile)) {
            getControllerTpl().process(data, docFileWriter);
        } catch (TemplateException ex) {
            ex.printStackTrace();
        }
        return Utils.streamToString(new FileInputStream(docFile));
    }

    private Template getControllerTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-controller.html.ftl");
    }

}
