package com.wugui.docs.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class Utils {

	public static String concat(String ...text) {
		StringBuffer content = new StringBuffer();
		Arrays.stream(text).forEach(content::append);
		return content.toString();
	}

	public static String concatJava(String text) {
		return concat(text, ".java");
	}

	/**
	 * 字符串按英文符号.分割成数组
	 * @param text
	 * @return
	 */
	public static String[] splitByDot(String text) {
		return StringUtils.split(text, "\\.");
	}
	public static String removeQuotation(String text) {
		return StringUtils.remove(text, "\"");
	}

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
	    try (InputStreamReader reader = new InputStreamReader(in, "utf-8");) {
			char[] buffer = new char[4096];
			int bytesRead = -1;
			while ((bytesRead = reader.read(buffer)) != -1) {
				stringBuilder.append(buffer, 0, bytesRead);
			}
		}
	    return stringBuilder.toString();
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
