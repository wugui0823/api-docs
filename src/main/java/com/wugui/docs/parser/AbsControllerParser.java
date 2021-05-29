package com.wugui.docs.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import com.wugui.docs.consts.ChangeFlag;
import com.wugui.docs.parser.block.BlockTagParserFactory;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.ParseUtils;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

public abstract class AbsControllerParser {
    /** java源文件 */
    private File javaFile;
    /** java源文件解析对象 */
    private CompilationUnit compilationUnit;
    /** 解析的结果 */
    private ControllerNode controllerNode;

    public ControllerNode parse(File javaFile, CompilationUnit compilationUnit) {
        this.javaFile = javaFile;
        this.compilationUnit = compilationUnit;
        String controllerName = javaFile.getName().split("\\.")[0];
        this.controllerNode = new ControllerNode(controllerName);
        compilationUnit.getClassByName(controllerName).ifPresent(c -> {
            extractBaseUrl(c);
            setClassInfo(c);
            parseMethodDocs(c);
        });
        controllerNode.setSrcFileName(javaFile.getAbsolutePath());
        return controllerNode;
    }

    File getControllerFile() {
        return javaFile;
    }

    ControllerNode getControllerNode() {
        return controllerNode;
    }

    /**
     * 解析并设置类文件信息(注释、包名、作者和描述等)
     * @param c
     */
    private void setClassInfo(ClassOrInterfaceDeclaration c) {
        // 设置类包名
        c.getParentNode().get().findFirst(PackageDeclaration.class).ifPresent(pd -> {
            controllerNode.setPackageName(pd.getNameAsString());
        });
        // 设置默认描述
        controllerNode.setDescription(c.getNameAsString());
        c.getJavadoc().ifPresent(d -> {
            String description = d.getDescription().toText();
            controllerNode.setDescription(StringUtils.isNotEmpty(description) ? description : c.getNameAsString());
            // 解析注释标签
            d.getBlockTags().forEach(tag -> BlockTagParserFactory.putData(controllerNode, tag));
        });
    }

    private void parseMethodDocs(ClassOrInterfaceDeclaration c) {
        // 获取所有public的方法
        c.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
                .forEach(m -> {
                    if (skipMethod(m)) {
                        return;
                    }
                    // 构建
                    RequestNode requestNode = new RequestNode();
                    requestNode.setControllerNode(controllerNode);
                    requestNode.setMethodName(m.getNameAsString());
                    requestNode.setUrl(controllerNode + File.separator + requestNode.getMethodName());
                    requestNode.setDescription(requestNode.getMethodName());
                    requestNode.setDeprecated(m.isAnnotationPresent(Deprecated.class));
                    // 解析方法的注释
                    m.getJavadoc().ifPresent(d -> {
                        requestNode.setDescription(d.getDescription().toText());
                        d.getBlockTags().forEach(e -> BlockTagParserFactory.putData(requestNode, e));
                    });
                    afterHandleMethod(requestNode, m);
                    // 获取方法返回类型
                    com.github.javaparser.ast.type.Type returnClassType = m.getType();
                    if (returnClassType == null) {
                        return;
                    }
                    ResponseNode responseNode = new ResponseNode(requestNode);
                    handleResponseNode(responseNode, returnClassType.getElementType());
                    requestNode.setResponseNode(responseNode);
                    setRequestNodeChangeFlag(requestNode);
                    controllerNode.addRequestNode(requestNode);
                });
    }

    /**
     * 获取Controller基础url
     * @param clazz
     */
    protected void extractBaseUrl(ClassOrInterfaceDeclaration clazz) {
    }

    abstract boolean skipMethod(MethodDeclaration m);

    /**
     * handle response object
     *
     * @param responseNode
     * @param resultType
     */
    protected void handleResponseNode(ResponseNode responseNode, com.github.javaparser.ast.type.Type resultType){
        parseClassNodeByType(responseNode, resultType);
    }

    void parseClassNodeByType(ClassNode classNode, com.github.javaparser.ast.type.Type classType){
        if (classType instanceof VoidType) {
            return;
        }
        // 解析方法返回类的泛型信息
        ((ClassOrInterfaceType) classType).getTypeArguments().ifPresent(typeList -> typeList.forEach(argType -> {
            GenericNode rootGenericNode = new GenericNode();
            rootGenericNode.setFromJavaFile(javaFile);
            rootGenericNode.setClassType(argType);
            classNode.addGenericNode(rootGenericNode);
        }));
        ParseUtils.parseClassNodeByType(javaFile, classNode, classType);
    }

    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
    }


    // 设置接口的类型（新/修改/一样）
    private void setRequestNodeChangeFlag(RequestNode requestNode) {
        List<ControllerNode> lastControllerNodeList = DocContext.getLastVersionControllerNodes();
        if (CollectionUtils.isEmpty(lastControllerNodeList)) {
            return;
        }
        for (ControllerNode lastControllerNode : lastControllerNodeList) {
            for (RequestNode lastRequestNode : lastControllerNode.getRequestNodes()) {
                if (lastRequestNode.getUrl().equals(requestNode.getUrl())) {
                    requestNode.setLastRequestNode(lastRequestNode);
                    requestNode.setChangeFlag(isSameRequestNodes(requestNode, lastRequestNode) ? ChangeFlag.SAME : ChangeFlag.MODIFY);
                    return;
                }
            }
        }
        requestNode.setChangeFlag(ChangeFlag.NEW);
    }

    private boolean isSameRequestNodes(RequestNode requestNode, RequestNode lastRequestNode) {
        for (String lastMethod : lastRequestNode.getMethod()) {
            if (!requestNode.getMethod().contains(lastMethod)) {
                return false;
            }
        }
        return Utils.toJson(requestNode.getParamNodes()).equals(Utils.toJson(lastRequestNode.getParamNodes()))
                && Utils.toJson(requestNode.getHeader()).equals(Utils.toJson(lastRequestNode.getHeader()))
                && requestNode.getResponseNode().toJsonApi().equals(lastRequestNode.getResponseNode().toJsonApi());
    }
}
