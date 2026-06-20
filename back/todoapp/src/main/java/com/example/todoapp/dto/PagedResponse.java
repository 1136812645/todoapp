package com.example.todoapp.dto;

import java.util.List;

public class PagedResponse<T> {
    private int code;
    private String message;
    private List<T> data;
    private long total;

    public PagedResponse() {}

    public PagedResponse(int code, String message, List<T> data, long total) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
