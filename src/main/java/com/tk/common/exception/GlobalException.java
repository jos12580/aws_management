package com.tk.common.exception;

/**
 * 全局异常
 * @Author wuXiaoMing
 * @Date 2022/10/8 13:50
 */
public class GlobalException extends RuntimeException{
    public GlobalException() {
    }

    public GlobalException(String message) {
        super(message);
    }
}
