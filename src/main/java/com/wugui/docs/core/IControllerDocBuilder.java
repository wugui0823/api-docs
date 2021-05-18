package com.wugui.docs.core;

import com.wugui.docs.parser.ControllerNode;

import java.io.IOException;

public interface IControllerDocBuilder {

    /**
     * build api docs and return as string
     *
     * @param controllerNode
     * @return
     */
    String buildDoc(ControllerNode controllerNode) throws IOException;

}
