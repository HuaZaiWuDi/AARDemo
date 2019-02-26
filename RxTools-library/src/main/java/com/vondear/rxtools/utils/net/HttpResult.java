package com.vondear.rxtools.utils.net;

import com.vondear.rxtools.model.JavaBean;

public class HttpResult<T> extends JavaBean {

    private int code;
    private String msg;
    private T data;

    public T getData() {
        return data == null ? (T) "" : data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

}
