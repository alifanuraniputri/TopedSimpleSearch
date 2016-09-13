package com.alifanurani.topedsimplesearch;

/**
 * Created by USER on 9/13/2016.
 */
public class SearchProductResultModel {

    private long id;

    private String nama;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SearchProductResultModel{" +
                "id=" + id +
                ", nama='" + nama + '\'' +
                '}';
    }
}

