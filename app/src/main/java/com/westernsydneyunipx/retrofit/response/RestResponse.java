package com.westernsydneyunipx.retrofit.response;

/**
 * @author PA1810.
 */

//receiving data from api data status and error or success message

public class RestResponse<T> {

    private T data;
    private int status;
    private String msg;

    public T data() {
        return data;
    }

    public int status() {
        return status;
    }

    public String msg() {
        return msg;
    }
}
