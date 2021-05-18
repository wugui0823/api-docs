package com.wugui.docs.core;

import com.wugui.docs.parser.AbsControllerParser;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.RequestNode;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.LogUtils;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsDocGenerator {

    private AbsControllerParser controllerParser;
    private IControllerDocBuilder controllerDocBuilder;
    private List<Link> docFileLinkList = new ArrayList<>();
    private List<ControllerNode> controllerNodeList = new ArrayList<>();

     AbsDocGenerator(AbsControllerParser controllerParser, IControllerDocBuilder controllerDocBuilder) {
        this.controllerParser = controllerParser;
        this.controllerDocBuilder = controllerDocBuilder;
        this.initControllerNodes();
    }

    /**
     * generate api Docs
     */
    public void generateDocs() {
        LogUtils.info("generate api docs start...");
        generateControllersDocs();
        generateIndex(controllerNodeList);
        LogUtils.info("generate api docs done !!!");
    }

    private void initControllerNodes(){
        File[] controllerFiles = DocContext.getControllerFiles();
        for (File controllerFile : controllerFiles) {
            LogUtils.info("start to parse controller file : %s", controllerFile.getName());
            ControllerNode controllerNode = controllerParser.parse(controllerFile);
            if (CollectionUtils.isEmpty(controllerNode.getRequestNodes())) {
                continue;
            }
            controllerNode.setSrcFileName(controllerFile.getAbsolutePath());
            final String docFileName = String.format("%s_%s.html", controllerNode.getPackageName().replace(".", "_"), controllerNode.getClassName());
            controllerNode.setDocFileName(docFileName);
            for (RequestNode requestNode : controllerNode.getRequestNodes()) {
                requestNode.setCodeFileUrl(String.format("%s#%s", docFileName, requestNode.getMethodName()));
            }
            controllerNodeList.add(controllerNode);
            LogUtils.info("success to parse controller file : %s", controllerFile.getName());
        }
    }

    private void generateControllersDocs() {
        File docPath = new File(DocContext.getDocPath());
        for (ControllerNode controllerNode : controllerNodeList) {
            try {
                LogUtils.info("start to generate docs for controller file : %s", controllerNode.getSrcFileName());
                final String controllerDocs = controllerDocBuilder.buildDoc(controllerNode);
                docFileLinkList.add(new Link(controllerNode.getDescription(), String.format("%s", controllerNode.getDocFileName())));
                Utils.writeToDisk(new File(docPath, controllerNode.getDocFileName()), controllerDocs);
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
