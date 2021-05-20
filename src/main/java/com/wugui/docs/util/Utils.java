package com.wugui.docs.util;

import com.alibaba.fastjson.JSONObject;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jdk.nashorn.internal.codegen.CompileUnit;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Utils {

    /**
     * object to pretty json
     * @param map
     * @return
     */
	public static String toPrettyJson(Object map){
		return JSONObject.toJSONString(map, true);
	}

    /**
     * object to simple json
     * @param map
     * @return
     */
	public static String toJson(Object map){
		return JSONObject.toJSONString(map);
    }

    /**
     * json string to object
     * @param json
     * @param type
     * @param <T>
     * @return
     */
	public static<T> T jsonToObject(String json, Class<T> type){
		return JSONObject.parseObject(json, type);
    }

	/**
	 * write content to file
	 * @param f
	 * @param content
	 * @throws IOException
	 */
	public static void writeToDisk(File f,String content) throws IOException{
		mkdirsForFile(f);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"utf-8"));
		writer.write(content);
		writer.close();
	}

	/**
	 * close stream
	 * @param stream
	 */
	public static void closeSilently(Closeable stream){
		if(stream != null){
			try{
				stream.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

    /**
     * simple read stream to String
     * @param in
     * @return
     * @throws IOException
     */
	public static String streamToString(InputStream in) throws IOException{
	    StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        char[] buffer = new char[4096];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, bytesRead);
        }
        reader.close();
	    return stringBuilder.toString();
    }

    /**
     * some parse url may has double quotation, remove them
     * @param rawUrl
     * @return
     */
    public static String removeQuotations(String rawUrl){
        return rawUrl.replace("\"","").trim();
    }

	/**
	 * remove some characters like [* \n]
	 * @param content
	 * @return
	 */
	public static String cleanCommentContent(String content){
		return content.replace("*","").replace("\n", "").trim();
	}

	/**
	 * get url with base url
	 * @param baseUrl
	 * @param relativeUrl
	 * @return
	 */
	public static String getActionUrl(String baseUrl, String relativeUrl){
		if(relativeUrl == null){
			return "";
		}
		if(baseUrl == null){
			return relativeUrl;
		}
		if(baseUrl.endsWith("/")){
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		if(!relativeUrl.startsWith("/")){
			relativeUrl = "/" + relativeUrl;
		}
		return baseUrl + relativeUrl;
	}

    /**
     * make first word lower case
     * @param name
     * @return
     */
	public static String decapitalize(String name) {
		if(StringUtils.isEmpty(name)) {
			return name;
		}
		if(name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char[] chars = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

    /**
	public static String capitalize(String name) {
		if(StringUtils.isEmpty(name)) {
			return name;
		}
		char[] chars = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

    /**
     * join string array , （ e.g. ([a,a,a] , .) = a.a.a )
     * @param array
     * @param separator
     * @return
     */
	public static String joinArrayString(String[] array, String separator){
		if(array == null || array.length == 0){
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0, len = array.length ; i != len ; i++){
			builder.append(array[i]);
			if(i != len -1){
				builder.append(separator);
			}
		}
		return builder.toString();
	}

    /**
     * get file name without extension
     * @param javaFile
     * @return
     */
	public static String getJavaFileName(File javaFile){
		String fileName = javaFile.getName();
		return javaFile.getName().substring(0, fileName.lastIndexOf("."));
	}

	public static Map<File, CompilationUnit> scan(File javaSrcDir) {
		Map<File, CompilationUnit> fileList = new HashMap<>();
		Stack<File> directoryStack = new Stack<>();
		directoryStack.push(javaSrcDir);
		while (!directoryStack.isEmpty()) {
			File file = directoryStack.pop();
			File[] childFileList = file.listFiles(f -> f.isDirectory() || StringUtils.endsWith(f.getName(), ".java"));
			for (File child : childFileList) {
				if (child.isDirectory()) {
					directoryStack.push(child);
					continue;
				}
				CompilationUnit unit = ParseUtils.compilationUnit(child);
				boolean validController = unit.findAll(ClassOrInterfaceDeclaration.class).stream()
						.anyMatch(cd -> cd.isAnnotationPresent(Controller.class) || cd.isAnnotationPresent(RestController.class));
				if (validController) {
					fileList.put(child, unit);
				}
			}
		}
		return fileList;
	}

	/**
	 * get simple class name
	 *
	 * @param packageClass
	 * @return
	 */
	public static String getClassName(String packageClass){
		String[] parts = packageClass.split("\\.");
		return parts[parts.length - 1];
	}

	/**
	 * create dirs for file
	 *
	 * @param file
	 */
	public static void mkdirsForFile(File file){
		if(file.isFile() && !file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
	}

	/**
	 * String转成File，如果不存在则创建
	 * @param path
	 * @return
	 */
	public static File createFileIfAbsent(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}
