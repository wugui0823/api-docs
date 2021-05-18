package com.wugui.docs.core;

import com.wugui.docs.config.DocsConfig;
import com.wugui.docs.service.DocContext;
import com.wugui.docs.util.CacheUtils;

/**
 *  main entrance
 */
public class Docs {

    /**
     * build html api docs
     */
    public static void buildHtmlDocs(DocsConfig config){
        DocContext.init(config);
        AbsDocGenerator docGenerator = new HtmlDocGenerator();
        DocContext.setControllerNodeList(docGenerator.getControllerNodeList());
        docGenerator.generateDocs();
        CacheUtils.saveControllerNodes(docGenerator.getControllerNodeList());
	}

}
