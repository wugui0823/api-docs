package com.wugui.docs.exception;

/**
 * @author yeguozhong yedaxia.github.com
 */
public class ConfigException extends RuntimeException{

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public static ConfigException create(String msg) {
        return new ConfigException(msg);
    }
}
