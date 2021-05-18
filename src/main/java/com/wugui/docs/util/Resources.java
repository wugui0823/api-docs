package com.wugui.docs.util;

import com.wugui.docs.service.DocContext;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Resources {

    private static boolean isDebug = false;
    private static String debugResourcePath;

    static {
        try {
            if(!isDebug){
                freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * get template file
     * @param fileName
     * @return
     */
    public static InputStream getTemplateFile(String fileName) throws FileNotFoundException{

        if(isDebug) {
            return new FileInputStream(new File(debugResourcePath, fileName));
        }

        final String userResPath = getUserResourcePath();

        if(getUserResourcePath() != null){
            File tplFile = new File(userResPath,fileName);
            if(tplFile.isFile() && tplFile.exists()) {
                return new FileInputStream(tplFile);
            }
        }

        return Resources.class.getResourceAsStream("/" + fileName);
    }

    /**
     * get code template file
     *
     * @param fileName
     * @return
     */
    public static InputStream getCodeTemplateFile(String fileName)throws FileNotFoundException{
        return getTemplateFile(fileName);
    }

    /**
     * get freemarker template
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Template getFreemarkerTemplate(String fileName) throws IOException {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_0);
        conf.setDefaultEncoding("utf-8");
        if(isDebug){
            conf.setDirectoryForTemplateLoading(new File(debugResourcePath));
        }else{
            final String userResPath = getUserResourcePath();
            File tplFile = new File(userResPath,fileName);
            if(tplFile.isFile() && tplFile.exists()) {
                conf.setDirectoryForTemplateLoading(new File(userResPath));
            }else {
                conf.setClassForTemplateLoading(Resources.class, "/");
            }
        }
        return conf.getTemplate(fileName);
    }

    private static String getUserResourcePath(){
        return DocContext.getDocsConfig().getResourcePath();
    }


    public static void setDebug(){
        isDebug = true;
        debugResourcePath = System.getProperty("user.dir") + "/build/resources/main";
    }
}
