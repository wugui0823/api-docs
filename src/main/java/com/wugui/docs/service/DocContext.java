package com.wugui.docs.service;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.wugui.docs.config.DocsConfig;
import com.wugui.docs.config.I18n;
import com.wugui.docs.exception.ConfigException;
import com.wugui.docs.parser.AbsControllerParser;
import com.wugui.docs.parser.ControllerNode;
import com.wugui.docs.parser.SpringControllerParser;
import com.wugui.docs.util.CacheUtils;
import com.wugui.docs.util.LogUtils;
import com.wugui.docs.util.ParseUtils;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 *
 * @author : zhangbo.chen
 * @since : 2021/5/12 10:21:00
 **/
public class DocContext {
    /** 项目路径 */
    private static String projectPath;
    /** 生产文档路径 */
    private static String docPath;
    //multi modules
    private static List<String> javaSrcPaths = new ArrayList<>();
    private static AbsControllerParser controllerParser;
    /** 扫描到的Controller集合 */
    private static List<File> controllerFiles;
    /** 配置类 */
    private static DocsConfig config;
    /** 国际化 */
    private static I18n i18n;
    /** api版本 */
    private static String currentApiVersion;
    /** 获取历史版本列表 */
    private static List<String> apiVersionList = new ArrayList<>();
    private static List<ControllerNode> lastVersionControllerNodes;
    private static List<ControllerNode> controllerNodeList;

    public static void init(DocsConfig config) {
        if (StringUtils.isEmpty(config.getApiVersion())) {
            throw new ConfigException("api version cannot be null");
        }
        if (StringUtils.isEmpty(config.getProjectName())) {
            config.setProjectName("api_docs");
        }
        DocContext.config = config;
        i18n = new I18n(config.getLocale());
        DocContext.currentApiVersion = config.getApiVersion();
        setProjectPath();
        setDocPath();
        initApiVersions();

        boolean isSetSrcPath = CollectionUtils.isEmpty(config.getJavaSrcPaths());
        boolean b = isSetSrcPath ? javaSrcPaths.add(getProjectPath()) : javaSrcPaths.addAll(config.getJavaSrcPaths());
        LogUtils.info("find java src paths:  %s", javaSrcPaths);
        findOutControllers();
        initLastVersionControllerNodes();
    }

    private static void initLastVersionControllerNodes() {
        File docDir = new File(docPath).getParentFile();
        File[] childDirs = docDir.listFiles(f -> f.isDirectory());
        if (ArrayUtils.isNotEmpty(childDirs)) {
            File lastVerDocDir = childDirs[0];
            for (File childDir : childDirs) {
                if (!StringUtils.equals(currentApiVersion, childDir.getName())
                        && childDir.lastModified() > lastVerDocDir.lastModified()) {
                    lastVerDocDir = childDir;
                }
            }
            if (lastVerDocDir != null) {
                lastVersionControllerNodes = CacheUtils.getControllerNodes(lastVerDocDir.getName());
            }
        }
    }

    /**
     * 获取文档目录下所有api版本
     */
    private static void initApiVersions() {
        File docDir = new File(docPath).getParentFile();
        String[] diffVersionApiDirs = docDir.list((dir, name) -> dir.isDirectory() && !name.startsWith("."));
        if (diffVersionApiDirs != null) {
            Collections.addAll(DocContext.apiVersionList, diffVersionApiDirs);
        }
    }

    private static void findOutControllers() {
        controllerFiles = new ArrayList<>();
        controllerParser = new SpringControllerParser();
        for (String javaSrcPath : getJavaSrcPaths()) {
            LogUtils.info("start find controllers in path : %s", javaSrcPath);
            File javaSrcDir = new File(javaSrcPath);
            List<File> files = Utils.scan(javaSrcDir, (f, name) -> f.getName().endsWith(".java") && ParseUtils.compilationUnit(f)
                    .findAll(ClassOrInterfaceDeclaration.class).stream()
                    .anyMatch(cd -> cd.isAnnotationPresent(Controller.class) || cd.isAnnotationPresent(RestController.class))
            );
            controllerFiles.addAll(files);
        }
    }

    /**
     * 设置日志文件
     * @return
     */
    public static File getLogFile() {
        File file = new File(DocContext.getDocPath() + "apiDoc.log");
        return file;
    }

    /**
     * get project path
     */
    public static String getProjectPath() {
        return projectPath;
    }

    /**
     * 设置项目路径
     */
    private static void setProjectPath() {
        if (StringUtils.isEmpty(config.getProjectPath())) {
            throw ConfigException.create("projectDir cannot be null");
        }
        File file = new File(config.getProjectPath());
        if (!file.exists()) {
            throw ConfigException.create("projectDir is not valid");
        }
        DocContext.projectPath = file.getAbsolutePath() + File.separator;
    }

    /**
     * api docs output path
     *
     * @return
     */
    public static String getDocPath() {
        return docPath;
    }

    /**
     * 设置文档输出路径
     */
    private static void setDocPath() {
        if (StringUtils.isEmpty(config.getDocsPath())) {
            throw ConfigException.create("docsPath cannot be null");
        }
        File docDir = Utils.createFileIfAbsent(config.getDocsPath()  + File.separator + config.getApiVersion());
        DocContext.docPath = docDir.getAbsolutePath() + File.separator;
    }

    /**
     * get java src paths
     *
     * @return
     */
    public static List<String> getJavaSrcPaths() {
        return javaSrcPaths;
    }


    /**
     * get all controllers in this project
     *
     * @return
     */
    public static File[] getControllerFiles() {
        return controllerFiles.toArray(new File[controllerFiles.size()]);
    }

    /**
     * get controller parser, it will return different parser by different framework you are using.
     *
     * @return
     */
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

    public static String getCurrentApiVersion() {
        return currentApiVersion;
    }

    public static List<String> getApiVersionList() {
        return apiVersionList;
    }

    public static List<ControllerNode> getLastVersionControllerNodes() {
        return lastVersionControllerNodes;
    }

    public static I18n getI18n() {
        return i18n;
    }

}
