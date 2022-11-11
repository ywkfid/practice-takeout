package com.ywk.takeout.common;

/**
 * 业务异常处理
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
