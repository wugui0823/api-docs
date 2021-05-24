package com.wugui.docs.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.wugui.docs.consts.ChangeFlag;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.ParseUtils;
import com.wugui.docs.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

public abstract class AbsControllerParser {

    private CompilationUnit compilationUnit;
    private ControllerNode controllerNode;
    private File javaFile;

    public ControllerNode parse(File javaFile, CompilationUnit compilationUnit) {
        this.javaFile = javaFile;
        this.compilationUnit = compilationUnit;
        this.controllerNode = new ControllerNode();
        String controllerName = Utils.getJavaFileName(javaFile);
        controllerNode.setClassName(controllerName);
        compilationUnit.getClassByName(controllerName)
                .ifPresent(c -> {
                    beforeHandleController(controllerNode, c);
                    parseClassDoc(c);
                    parseMethodDocs(c);
                });

        return controllerNode;
    }

    File getControllerFile() {
        return javaFile;
    }

    ControllerNode getControllerNode() {
        return controllerNode;
    }

    /**
     * 解析类文件注释(作者和描述)
     * @param c
     */
    private void parseClassDoc(ClassOrInterfaceDeclaration c) {
        c.getParentNode().get().findFirst(PackageDeclaration.class).ifPresent(pd -> {
            controllerNode.setPackageName(pd.getNameAsString());
        });
        // 设置默认描述
        controllerNode.setDescription(c.getNameAsString());
        c.getJavadoc().ifPresent(d -> {
            String description = d.getDescription().toText();
            controllerNode.setDescription(StringUtils.isNotEmpty(description) ? description : c.getNameAsString());
            if (CollectionUtils.isEmpty(d.getBlockTags())) {
                return;
            }
            for (JavadocBlockTag blockTag : d.getBlockTags()) {
                if ("author".equalsIgnoreCase(blockTag.getTagName())) {
                    controllerNode.setAuthor(blockTag.getContent().toText());
                } else if ("description".equalsIgnoreCase(blockTag.getTagName())) {
                    controllerNode.setDescription(blockTag.getContent().toText());
                }
            }
        });
    }

    private void parseMethodDocs(ClassOrInterfaceDeclaration c) {
        c.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
                .forEach(m -> {
                    if(shouldIgnoreMethod(m)){
                        return;
                    }
                    RequestNode requestNode = new RequestNode();
                    requestNode.setControllerNode(controllerNode);
                    requestNode.setMethodName(m.getNameAsString());
                    requestNode.setUrl(requestNode.getMethodName());
                    requestNode.setDescription(requestNode.getMethodName());
                    requestNode.setDeprecated(m.isAnnotationPresent(Deprecated.class));
                    m.getJavadoc().ifPresent(d -> {
                        String description = d.getDescription().toText();
                        requestNode.setDescription(description);
                        List<JavadocBlockTag> blockTagList = d.getBlockTags();
                        String value = null;
                        for (JavadocBlockTag blockTag : blockTagList) {
                            value = blockTag.getContent().toText();
                            if (StringUtils.equalsIgnoreCase("param", blockTag.getTagName())) {
                                requestNode.addParamNode(new ParamNode(blockTag.getName().orElse(null), value));
                            } else if (StringUtils.equalsIgnoreCase("author", blockTag.getTagName())) {
                                requestNode.setAuthor(value);
                            } else if(StringUtils.equalsIgnoreCase("description", blockTag.getTagName())){
                                requestNode.setSupplement(value);
                            }
                        }
                    });

                    m.getParameters().forEach(p -> {
                        String paramName = p.getName().asString();
                        ParamNode paramNode = requestNode.getParamNodeByName(paramName);
                        if (paramNode == null || ParseUtils.isExcludeParam(p)) {
                            requestNode.getParamNodes().remove(paramNode);
                            return;
                        }
                        Type pType = p.getType();
                        boolean isList = false;
                        if(pType instanceof ArrayType){
                            isList = true;
                            pType = ((ArrayType) pType).getComponentType();
                        }else if(ParseUtils.isCollectionType(pType.asString())){
                            List<ClassOrInterfaceType> collectionTypes = pType.findAll(ClassOrInterfaceType.class);
                            isList = true;
                            if(!collectionTypes.isEmpty()){
                                pType = collectionTypes.get(0);
                            }else{
                                paramNode.setType("Object[]");
                            }
                        }else{
                            pType = p.getType();
                        }
                        if(paramNode.getType() == null){
                            if(ParseUtils.isEnum(getControllerFile(), pType.asString())){
                                paramNode.setType(isList ? "enum[]": "enum");
                            }else{
                                final String pUnifyType = ParseUtils.unifyType(pType.asString());
                                paramNode.setType(isList ? pUnifyType + "[]": pUnifyType);
                            }
                        }
                    });

                    com.github.javaparser.ast.type.Type resultClassType = m.getType();
                    String stringResult = null;
                    afterHandleMethod(requestNode, m);

                    if (resultClassType == null) {
                        return;
                    }
                    ResponseNode responseNode = new ResponseNode(requestNode);
                    handleResponseNode(responseNode, resultClassType.getElementType());
                    requestNode.setResponseNode(responseNode);
                    setRequestNodeChangeFlag(requestNode);
                    controllerNode.addRequestNode(requestNode);
                });
    }

    /**
     * called before controller node has handled
     *
     * @param clazz
     */
    protected void beforeHandleController(ControllerNode controllerNode, ClassOrInterfaceDeclaration clazz) {
    }

    abstract boolean shouldIgnoreMethod(MethodDeclaration m);

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

    /**
     * called after request method node has handled
     */
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
    }


    // 设置接口的类型（新/修改/一样）
    private void setRequestNodeChangeFlag(RequestNode requestNode) {
        List<ControllerNode> lastControllerNodeList = DocContext.getLastVersionControllerNodes();
        if (lastControllerNodeList == null || lastControllerNodeList.isEmpty()) {
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
