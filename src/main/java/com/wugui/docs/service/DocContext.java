package com.wugui.docs.service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.wugui.docs.config.DocsConfig;
import com.wugui.docs.exception.ConfigException;
import com.wugui.docs.parser.AbsControllerParser;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.SpringControllerParser;
import com.wugui.docs.util.CacheUtils;
import com.wugui.docs.util.LogUtils;
import com.wugui.docs.util.ParseUtils;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author : zhangbo.chen
 * @since : 2021/5/12 10:21:00
 **/
public class DocContext {
    /** 生产文档路径 */
    private static String docPath;
    private static List<String> javaSrcPaths = new ArrayList<>();
    private static AbsControllerParser controllerParser;
    /** 扫描到的Controller集合 */
    private static Map<File, CompilationUnit> compilationUnitMap = new HashMap<>();
    /** 配置类 */
    private static DocsConfig config;
    /** api版本 */
    private static String currentVersion;
    /** 获取历史版本列表 */
    private static List<String> versionList = new ArrayList<>();
    private static List<ControllerNode> lastVersionControllerNodes;
    private static List<ControllerNode> controllerNodeList;

    public static void init(DocsConfig config) {
        DocContext.config = config;
        configCheck();
        currentVersion = config.getVersion();
        setDocPath();
        obtainVersionHistoryList();
        javaSrcPaths.addAll(config.getJavaSrcPaths());
        controllerParser = new SpringControllerParser();
        findAndParseController();
        obtainNewerVersionController();
    }

    private static void obtainNewerVersionController() {
        File docDir = new File(docPath).getParentFile();
        File[] childDirs = docDir.listFiles(f -> f.isDirectory());
        if (ArrayUtils.isNotEmpty(childDirs)) {
            List<String> collect = Arrays.stream(childDirs).map(f -> f.getName())
                    .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (String childDir : collect) {
                if (!StringUtils.equals(currentVersion, childDir)) {
                    lastVersionControllerNodes = CacheUtils.getControllerNodes(childDir);
                    break;
                }
            }
        }
    }
    private static void configCheck() {
        if (StringUtils.isEmpty(config.getVersion())) {
            throw new ConfigException("version cannot be null");
        }
        if (StringUtils.isEmpty(config.getProjectName())) {
            throw new ConfigException("projectName cannot be null");
        }
        if (StringUtils.isEmpty(config.getDocsPath())) {
            throw ConfigException.create("docsPath cannot be null");
        }
    }
    /**
     * 获取历史版本列表
     */
    private static void obtainVersionHistoryList() {
        File docDir = new File(docPath).getParentFile();
        String[] history = docDir.list((dir, name) -> dir.isDirectory() && !name.startsWith("."));
        if (history != null) {
            Collections.addAll(DocContext.versionList, history);
        }
    }

    private static void findAndParseController() {
        for (String javaSrcPath : getJavaSrcPaths()) {
            LogUtils.info("start find controllers in path : %s", javaSrcPath);
            Collection<File> files = FileUtils.listFiles(new File(javaSrcPath), new String[]{"java"}, true);
            if (CollectionUtils.isNotEmpty(files)) {
                files.forEach(f -> {
                    CompilationUnit unit = ParseUtils.compilationUnit(f);
                    boolean validController = unit.findAll(ClassOrInterfaceDeclaration.class).stream()
                            .anyMatch(cd -> cd.isAnnotationPresent(Controller.class) || cd.isAnnotationPresent(RestController.class));
                    if (validController) {
                        compilationUnitMap.put(f, unit);
                    }
                });
            }
        }
    }

    /**
     * 设置日志文件
     * @return
     */
    public static File getLogFile() {
        return new File(DocContext.getDocPath() + "apiDoc.log");
    }

    public static String getDocPath() {
        return docPath;
    }

    /**
     * 设置文档输出路径
     */
    private static void setDocPath() {
        File docDir = Utils.createFileIfAbsent(config.getDocsPath()  + File.separator + config.getVersion());
        DocContext.docPath = docDir.getAbsolutePath() + File.separator;
    }

    public static List<String> getJavaSrcPaths() {
        return javaSrcPaths;
    }

    public static Map<File, CompilationUnit> getCompilationUnitMap() {
        return compilationUnitMap;
    }

    public static AbsControllerParser controllerParser() {
        return controllerParser;
    }

    public static List<ControllerNode> getControllerNodeList() {
        return controllerNodeList;
    }

    public static void setControllerNodeList(List<ControllerNode> controllerNodeList) {
        DocContext.controllerNodeList = controllerNodeList;
    }

    public static DocsConfig getDocsConfig() {
        return DocContext.config;
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static List<String> getVersionList() {
        return versionList;
    }

    public static List<ControllerNode> getLastVersionControllerNodes() {
        return lastVersionControllerNodes;
    }

}
