package com.alifanurani.topedsimplesearch.API;

import com.alifanurani.topedsimplesearch.SearchModel.SearchProductResultModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by USER on 9/13/2016.
 */
public interface Api {

    @GET("search/v1/product")
    Call<SearchProductResultModel> search(
            @Query("q") String q);

    @GET("search/v1/product")
    Call<SearchProductResultModel> searchPagination(
            @Query("q") String q,@Query("start") int start, @Query("rows") int rows);
}
