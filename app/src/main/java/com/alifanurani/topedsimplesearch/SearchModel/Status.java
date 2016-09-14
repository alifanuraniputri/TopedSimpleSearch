package com.alifanurani.topedsimplesearch.SearchModel;

/**
 * Created by USER on 9/14/2016.
 */
public class Status {

    private long error_code;
    private String message;

    public long getError_code() {
        return error_code;
    }

    public void setError_code(long error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "error_code=" + error_code +
                ", message='" + message + '\'' +
                '}';
    }
}
