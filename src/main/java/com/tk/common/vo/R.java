package com.tk.common.vo;

import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class R<T>  implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 业务错误码
     */
    private int status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private String msg;

    public static <T> R<T> ok() {
        return R.ok(null, "success");
    }


    public boolean isOk() {
        return HttpStatus.HTTP_OK <= status && status <= HttpStatus.HTTP_MULT_CHOICE;
    }

    public static <T> R<T> ok(T data) {
        return ok(data, HttpStatus.HTTP_OK, "success");
    }

    public static <T> R<T> ok(T data, String msg) {
        return ok(data, HttpStatus.HTTP_OK, msg);
    }

    public static <T> R<T> fail(String msg) {
        return ok(null, HttpStatus.HTTP_INTERNAL_ERROR, msg);
    }

    public static <T> R<T> fail() {
        return ok(null, HttpStatus.HTTP_INTERNAL_ERROR, "error");
    }

    public static <T> R<T> fail(int status, String msg) {
        return ok(null, status, msg);
    }

    public static <T> R<T> ok(T data, int status, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setStatus(status);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }


}
