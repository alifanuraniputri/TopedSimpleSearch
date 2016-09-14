package com.alifanurani.topedsimplesearch;

import java.util.Arrays;

/**
 * Created by USER on 9/13/2016.
 */
public class SearchProductResultModel {

    private Status status;
    private Data data[];

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SearchProductResultModel{" +
                "status=" + status +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

