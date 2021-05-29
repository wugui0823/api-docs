package com.wugui.docs.config;

import java.util.ArrayList;
import java.util.List;

public class DocsConfig {

    List<String> javaSrcPaths = new ArrayList<>();
    String docsPath;
    String rootPath;
    String version;
    String projectName;
    // 是否开启对象反射
    Boolean openReflection = Boolean.TRUE;

    public void setDocsPath(String docsPath) {
        this.docsPath = docsPath;
    }

    public String getDocsPath() {
        return docsPath;
    }

    public List<String> getJavaSrcPaths() {
        return javaSrcPaths;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void addJavaSrcPath(String javaSrcPath){
        javaSrcPaths.add(javaSrcPath);
    }

    public Boolean getOpenReflection() {
        return openReflection;
    }

    public void setOpenReflection(Boolean openReflection) {
        this.openReflection = openReflection;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
