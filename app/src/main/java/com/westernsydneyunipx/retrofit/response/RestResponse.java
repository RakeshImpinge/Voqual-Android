package com.westernsydneyunipx.retrofit.response;

/**
 * @author PA1810.
 */

//receiving data from api data status and error or success message

public class RestResponse<T> {

    private T data;
    private int status;
    private String msg;
    private String access_token;

    public T data() {
        return data;
    }

    public int status() {
        return status;
    }

    public String msg() {
        return msg;
    }

    public String getAccess_token(){
        return access_token;
    }
}
