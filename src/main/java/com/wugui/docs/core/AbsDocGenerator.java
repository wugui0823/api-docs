package com.wugui.docs.core;

import com.github.javaparser.ast.CompilationUnit;
import com.wugui.docs.parser.AbsControllerParser;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.RequestNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.LogUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsDocGenerator {

    private AbsControllerParser controllerParser;
    private IControllerDocBuilder controllerDocBuilder;
    private List<Link> docFileLinkList = new ArrayList<>();
    private List<ControllerNode> controllerNodeList = new ArrayList<>();

     AbsDocGenerator(AbsControllerParser controllerParser, IControllerDocBuilder controllerDocBuilder) {
        this.controllerParser = controllerParser;
        this.controllerDocBuilder = controllerDocBuilder;
        this.parseControllerNodes();
    }

    /**
     * generate api Docs
     */
    public void generateDocs() {
        generateControllersDocs();
        generateIndex(controllerNodeList);
    }

    private void parseControllerNodes(){
        Map<File, CompilationUnit> controllerFiles = DocContext.getCompilationUnitMap();
        for (Map.Entry<File, CompilationUnit> entry : controllerFiles.entrySet()) {
            LogUtils.info("start to parse controller file : %s", entry.getKey().getName());
            ControllerNode controllerNode = controllerParser.parse(entry.getKey(), entry.getValue());
            if (CollectionUtils.isEmpty(controllerNode.getRequestNodes())) {
                continue;
            }
            // api对应的html文档名称
            final String docFileName = String.format("%s_%s.html", controllerNode.getPackageName().replace(".", "_"), controllerNode.getClassName());
            controllerNode.setDocFileName(docFileName);
            for (RequestNode requestNode : controllerNode.getRequestNodes()) {
                requestNode.setCodeFileUrl(String.format("%s#%s", docFileName, requestNode.getMethodName()));
            }
            controllerNodeList.add(controllerNode);
            LogUtils.info("success to parse controller file : %s", entry.getKey().getName());
        }
    }

    private void generateControllersDocs() {
        File docPath = new File(DocContext.getDocPath());
        for (ControllerNode controllerNode : controllerNodeList) {
            try {
                LogUtils.info("start to generate docs for controller file : %s", controllerNode.getSrcFileName());
                final String controllerDocs = controllerDocBuilder.buildDoc(controllerNode);
                docFileLinkList.add(new Link(controllerNode.getDescription(), String.format("%s", controllerNode.getDocFileName())));
                FileUtils.writeStringToFile(new File(docPath, controllerNode.getDocFileName()), controllerDocs, "utf-8");
                LogUtils.info("success to generate docs for controller file : %s", controllerNode.getSrcFileName());
            } catch (IOException e) {
                LogUtils.error("generate docs for controller file : " + controllerNode.getSrcFileName() + " fail", e);
            }
        }
    }

    public List<ControllerNode> getControllerNodeList() {
        return controllerNodeList;
    }

    abstract void generateIndex(List<ControllerNode> controllerNodeList);
}
