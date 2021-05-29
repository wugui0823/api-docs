package com.wugui.docs.parser;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.wugui.docs.parser.annotation.ClassMappingParserFactory;
import com.wugui.docs.parser.annotation.MethodMappingParserFactory;
import com.wugui.docs.parser.annotation.ParamMappingParserFactory;
import com.wugui.docs.util.ParseUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpringControllerParser extends AbsControllerParser {

    @Override
    protected void extractBaseUrl(ClassOrInterfaceDeclaration clazz) {
        // 如果有RequestMapping注解，获取自定义的路径
        clazz.getAnnotationByClass(RequestMapping.class).ifPresent(e ->
                ClassMappingParserFactory.parse(getControllerNode(), e)
        );
    }

    @Override
    protected boolean skipMethod(MethodDeclaration m) {
        // 如果不存在@xxxMapping,则跳过解析该方法
        long mappingCount = m.getAnnotations().stream()
                .filter(e -> StringUtils.endsWith(e.getNameAsString(), "Mapping")).count();
        return mappingCount > 0 ? false : true;
    }

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
        // 解析方法注解
        md.getAnnotations().forEach(an -> {
            String name = an.getNameAsString();
            // 解析@xxxMapping注解
            if (StringUtils.endsWith(name, "Mapping")) {
                // 获取Http提交方式
                String method = StringUtils.remove(name, "Mapping").toUpperCase();
                // 如果不是RequestMapping注解，前缀就是method，如果@GetMapping
                if (!StringUtils.equals("REQUEST", method)) {
                    requestNode.addMethod(RequestMethod.valueOf(method).name());
                }
                // 解析@XxxMapping注解,设置url、header、method等
                MethodMappingParserFactory.parse(requestNode, an);
            }
        });

        md.getParameters().forEach(p -> {
            String paramName = p.getNameAsString();
            ParamNode paramNode = requestNode.getParamNodeByName(paramName);
            if (paramNode == null) {
                return;
            }
            // 删除Servlet类型的入参
            if (ParseUtils.isServletObject(p.getTypeAsString())) {
                requestNode.getParamNodes().remove(paramNode);
                return;
            }
            // 参数类型
            paramNode.setType(ParseUtils.getParamType(p));
            // 解析参数注解
            p.getAnnotations().forEach(an -> {
                String name = an.getNameAsString();
                // 非空校验
                String[] notNull = {"NotNull", "NotBlank", "NotEmpty"};
                if (ArrayUtils.contains(notNull, name)) {
                    paramNode.setRequired(true);
                    return;
                }
                String[] paramAnnotations = {"RequestParam", "RequestBody", "PathVariable"};
                if (!ArrayUtils.contains(paramAnnotations, name)) {
                    return;
                }
                if ("RequestBody".equals(name)) {
                    setRequestBody(paramNode, p.getType());
                }
                // 解析参数注解
                ParamMappingParserFactory.parse(paramNode, an);
            });
            //如果参数没有加注解且是自定义对象
            if (!paramNode.getJsonBody() && ParseUtils.isModelType(paramNode.getType())) {
                ClassNode classNode = new ClassNode();
                parseClassNodeByType(classNode, p.getType());
                List<ParamNode> paramNodeList = new ArrayList<>();
                toParamNodeList(paramNodeList, classNode, "");
                requestNode.getParamNodes().remove(paramNode);
                requestNode.getParamNodes().addAll(paramNodeList);
            }
        });
    }

    @Override
    protected void handleResponseNode(ResponseNode responseNode, Type resultType) {
        if (resultType instanceof ClassOrInterfaceType) {
            String className = ((ClassOrInterfaceType) resultType).getName().getIdentifier();
            if ("org.springframework.http.ResponseEntity".endsWith(className)) {
                Optional<NodeList<Type>> nodeListOptional = ((ClassOrInterfaceType) resultType).getTypeArguments();
                if (nodeListOptional.isPresent()) {
                    NodeList<Type> typeNodeList = nodeListOptional.get();
                    if (!typeNodeList.isEmpty()) {
                        resultType = typeNodeList.get(0).getElementType();
                    }
                } else {
                    responseNode.setClassName(className);
                    return;
                }
            }
        }
        super.handleResponseNode(responseNode, resultType);
    }

    private void setRequestBody(ParamNode paramNode, Type paramType) {
        if (ParseUtils.isModelType(paramType.asString())) {
            ClassNode classNode = new ClassNode();
            parseClassNodeByType(classNode, paramType);
            paramNode.setJsonBody(true);
            classNode.setShowFieldNotNull(Boolean.TRUE);
            paramNode.setDescription(classNode.toJsonApi());
        }
    }

    private void toParamNodeList(List<ParamNode> paramNodeList, ClassNode formNode, String parentName) {
        formNode.getChildNodes().forEach(filedNode -> {
            if (filedNode.getChildNode() != null) {
                toParamNodeList(paramNodeList, filedNode.getChildNode(), filedNode.getName() + ".");
            } else {
                ParamNode paramNode = new ParamNode();
                paramNode.setName(parentName + filedNode.getName());
                paramNode.setType(filedNode.getType());
                paramNode.setDescription(filedNode.getDescription());
                paramNode.setRequired(filedNode.getNotNull());
                paramNodeList.add(paramNode);
            }
        });
    }
}
