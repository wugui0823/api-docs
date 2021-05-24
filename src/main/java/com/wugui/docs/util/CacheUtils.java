package com.wugui.docs.util;

import com.alibaba.fastjson.JSONObject;
import com.wugui.docs.parser.ClassNode;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.ResponseNode;
import com.wugui.docs.service.DocContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * cache all controller nodes
 *
 * @author yeguozhong yedaxia.github.com
 */
public class CacheUtils {

    private static final String CACHE_FILE = ".cache.json";

    /**
     * save controller nodes of this version
     *
     * @param controllerNodes
     */
    public static void saveControllerNodes(List<ControllerNode> controllerNodes) {
        try {
            controllerNodes.forEach(controllerNode -> {
                controllerNode.getRequestNodes().forEach(requestNode -> {
                    requestNode.setControllerNode(null);
                    requestNode.setLastRequestNode(null);
                    ResponseNode responseNode = requestNode.getResponseNode();
                    responseNode.setRequestNode(null);
                    removeLoopNode(responseNode);
                });
            });
            Utils.writeToDisk(new File(DocContext.getDocPath(), CACHE_FILE), Utils.toJson(controllerNodes));
        } catch (Exception ex) {
            LogUtils.error("saveControllerNodes error!!!", ex);
        }
    }

    private static void removeLoopNode(ClassNode classNode) {
        classNode.setParentNode(null);
        classNode.setGenericNodes(null);
        classNode.getChildNodes().forEach(fieldNode -> {
            fieldNode.setClassNode(null);
            if (fieldNode.getChildNode() != null) {
                removeLoopNode(fieldNode.getChildNode());
            }
        });
    }

    /**
     * get controller nodes of api version
     *
     * @param apiVersion
     * @return return null if cache not exists
     */
    public static List<ControllerNode> getControllerNodes(String apiVersion) {
        File apiRootPath = new File(new File(DocContext.getDocPath()).getParentFile(), apiVersion);
        if (!apiRootPath.exists()) {
            return null;
        }
        File cacheFile = new File(apiRootPath, CACHE_FILE);
        if (!cacheFile.exists()) {
            return null;
        }
        try {
            String cacheStr = Utils.streamToString(new FileInputStream(cacheFile));
            ControllerNode[] controllerNodes = JSONObject.parseObject(cacheStr, ControllerNode[].class);
            return Arrays.asList(controllerNodes);
        } catch (IOException ex) {
            LogUtils.error("get ControllerNodes error!!!", ex);
            return null;
        }
    }
}
