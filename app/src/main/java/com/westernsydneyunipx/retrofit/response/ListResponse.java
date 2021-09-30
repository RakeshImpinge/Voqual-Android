package com.westernsydneyunipx.retrofit.response;

import java.util.ArrayList;

/**
 * @author PA1810.
 */

//receiving data from api data status and error or success message

public class ListResponse<T> {

    private ArrayList<T> data;
    private int status;
    private String msg;

    public ArrayList<T> data() {
        return data;
    }

    public int status() {
        return status;
    }

    public String msg() {
        return msg;
    }
}
