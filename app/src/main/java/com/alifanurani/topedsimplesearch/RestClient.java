package com.alifanurani.topedsimplesearch;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by USER on 9/13/2016.
 */
public class RestClient {
    private static RestClient mRestClient = null;
    private Retrofit mRestAdapter = null;

    private static RestClient getInstance(){
        if(mRestClient == null)
        {
            mRestClient = new RestClient();
            mRestClient.initializeRestAdapter();
        }
        return mRestClient;
    }

    public static Retrofit getRestAdapter(){
        return getInstance().mRestAdapter;
    }

    private void initializeRestAdapter(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();

        mRestAdapter = new Retrofit.Builder().baseUrl("http://ace.tokopedia.com/").client(client).addConverterFactory(GsonConverterFactory.create()).build();
    }
}
