package com.wugui.docs.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocsConfig {

    String projectPath; // must set
    /** 源码路径 */
    List<String> javaSrcPaths = new ArrayList<>();
    String docsPath; // default equals projectPath
    String resourcePath; // if empty, use the default resources
    String mvcFramework; //spring, play, jfinal, generic, can be empty
    String apiVersion; // this api version
    String projectName; //project name
    Boolean autoGenerate = Boolean.FALSE; // 自动生成所有Controller的接口文档，不需要@ApiDoc注解
    Locale locale = Locale.getDefault();
    Boolean openReflection = Boolean.TRUE; // 是否开启对象反射

    String rapHost;
    String rapLoginCookie;
    String rapProjectId;
    String rapAccount;
    String rapPassword;

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setDocsPath(String docsPath) {
        this.docsPath = docsPath;
    }

    public String getDocsPath() {
        return docsPath;
    }

    public void setMvcFramework(String mvcFramework) {
        this.mvcFramework = mvcFramework;
    }

    public List<String> getJavaSrcPaths() {
        return javaSrcPaths;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Boolean getAutoGenerate() {
        return autoGenerate;
    }

    public void setAutoGenerate(Boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * if cannot find the java file from other module, you can try to config the java src path yourself.
     *
     * @param javaSrcPath
     */
    public void addJavaSrcPath(String javaSrcPath){
        javaSrcPaths.add(javaSrcPath);
    }

    public String getRapHost() {
        return rapHost;
    }

    public String getRapLoginCookie() {
        return rapLoginCookie;
    }

    public String getRapProjectId() {
        return rapProjectId;
    }

    public String getRapAccount() {
        return rapAccount;
    }

    public String getRapPassword() {
        return rapPassword;
    }

    public Boolean getOpenReflection() {
        return openReflection;
    }

    public void setOpenReflection(Boolean openReflection) {
        this.openReflection = openReflection;
    }
}
