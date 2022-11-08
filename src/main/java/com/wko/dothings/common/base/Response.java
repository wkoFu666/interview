package com.wko.dothings.common.base;

/**
 * 封装 接口返回的信息
 */
public class Response {

    private static final int CODE_SUCCESS = 0;
    private static final int CODE_ERROR = -1;
    private static final String MSG_SUCCESS = "操作成功";
    private static final String MSG_ERROR = "操作失败";
    private int code = 0;
    private String msg;
    private Object data;
    private Page page;

    private Response() {
    }

    private Response(int code, String msg, Object data, Page page) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.page = page;
    }

    public static Response ok() {
        return new Response(0, "操作成功", (Object) null, (Page) null);
    }

    public static Response ok(Object data) {
        return new Response(0, "操作成功", data, (Page) null);
    }

    public static Response ok(Object data, Page page) {
        return new Response(0, "操作成功", data, page);
    }

    public static Response ok(String message, Object data, Page page) {
        return new Response(0, message, data, page);
    }

    public static Response error() {
        return new Response(-1, "操作失败", (Object) null, (Page) null);
    }

    public static Response error(Object data) {
        return new Response(-1, "操作失败", data, (Page) null);
    }

    public static Response error(Object data, Page page) {
        return new Response(-1, "操作失败", data, page);
    }

    public static Response error(String message, Object data, Page page) {
        return new Response(-1, message, data, page);
    }

    public static Response error(int code, String message) {
        return new Response(code, message, (Object) null, (Page) null);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Page getPage() {
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
