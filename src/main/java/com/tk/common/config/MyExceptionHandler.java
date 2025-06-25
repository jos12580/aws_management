package com.tk.common.config;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.tk.common.exception.GlobalException;
import com.tk.common.util.IpUtils;
import com.tk.common.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationNotSupportedException;

/**
 * @Author wuXiaoMing
 * @Date 2022/10/12 17:06
 */
@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {


    @ExceptionHandler(value = GlobalException.class)
    public R<Object> globalExceptionHandler(Exception e, HttpServletRequest request) {
        log.error(" 发生未知错误！！！" + JSON.toJSONString(request.getParameterMap()));
        log.error("全局异常！原因是:{},ip:{},请求URL:{}", e.getMessage(), IpUtils.getIpAddress(request), request.getRequestURI());
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(value = AuthenticationNotSupportedException.class)
    public R<Object> loginExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("登录异常！原因是:{},ip:{},请求URL:{}", e.getMessage(),IpUtils.getIpAddress(request),request.getRequestURI());
        return R.fail(HttpStatus.HTTP_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(value = ValidationException.class)
    public R<Object> validationExceptionHandler(Exception e) {
        log.error("参数错误！原因是=>{}", e.getMessage());
        return R.fail("参数错误! "+e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public R<Object> exceptionHandler(Exception e, HttpServletRequest request) {
        log.error(" 发生未知错误！！！"+ JSON.toJSONString(request.getParameterMap()));
        log.error("未知异常！原因是:{},ip:{},请求URL:{}", e.getMessage(),IpUtils.getIpAddress(request),request.getRequestURI());
        log.error("",e);
        return R.fail("未知错误！！！");
    }

}
