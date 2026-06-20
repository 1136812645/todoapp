package com.example.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountResponse {
    private int code;
    private String message;

    @JsonProperty("deleted_count")
    private long deletedCount;

    public CountResponse() {}

    public CountResponse(int code, String message, long deletedCount) {
        this.code = code;
        this.message = message;
        this.deletedCount = deletedCount;
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

    public long getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(long deletedCount) {
        this.deletedCount = deletedCount;
    }
}
