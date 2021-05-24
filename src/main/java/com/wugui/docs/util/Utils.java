package com.wugui.docs.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
     * get file name without extension
     * @param javaFile
     * @return
     */
	public static String getJavaFileName(File javaFile){
		String fileName = javaFile.getName();
		return javaFile.getName().substring(0, fileName.lastIndexOf("."));
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
