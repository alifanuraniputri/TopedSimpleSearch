package com.alifanurani.topedsimplesearch;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.alifanurani.topedsimplesearch.API.Api;
import com.alifanurani.topedsimplesearch.API.RestClient;
import com.alifanurani.topedsimplesearch.ActiveAndroidModel.Product;
import com.alifanurani.topedsimplesearch.Adapter.ProductAdapter;
import com.alifanurani.topedsimplesearch.SearchModel.Data;
import com.alifanurani.topedsimplesearch.SearchModel.SearchProductResultModel;
import com.alifanurani.topedsimplesearch.Utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USER on 9/15/2016.
 */
public class MainPresenter {

    private ArrayList<Data> datas;
    private String query;
    private int pageNow;
    private Callback<SearchProductResultModel> mCallback;
    private Api mService;

    private View view;

    public MainPresenter(View view) {
        mService = RestClient.getRestAdapter().create(Api.class);
        mCallback = callbackSearch();
        datas = new ArrayList<>();
        this.view = view;
    }

    public void onTakeView(View view) {
        this.view = view;
    }

    public ArrayList<Data> getDatas() {
        return datas;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public void searchProductAPI(String q) {
        datas = new ArrayList<>();
        query = q;
        Call<SearchProductResultModel> searchProductCall = mService.searchPagination(query,1,10);
        searchProductCall.enqueue(mCallback);
        mCallback = callbackSearch();
    }

    public void initSearchProductDB(String q) {
        datas = new ArrayList<>();
        query = q;
    }

    public void selectProductDB() {
        List<Product> productsDb= Product.get10(query,pageNow*10);
        if (productsDb.size()>0) {
            for (int i=0; i<productsDb.size(); i++) {
                datas.add(new Data(productsDb.get(i).name, productsDb.get(i).image_uri, productsDb.get(i).price ));
            }
        } else {
            Call<SearchProductResultModel> searchProductCall12= mService.searchPagination(query,pageNow*10+1,10);
            searchProductCall12.enqueue(mCallback);
        }
        view.updateView();
    }

    public Callback<SearchProductResultModel> callbackSearch(){
        return new Callback<SearchProductResultModel>() {
            @Override
            public void onResponse(Call<SearchProductResultModel> call, Response<SearchProductResultModel> response) {
                if ( response.body() != null && response.body().getStatus().getError_code() == 0) {
                    Log.d("TEST", "response.body() != null && response.body().getStatus().getError_code() == 0");
                    Data[] moreProducts = response.body().getData();
                    for (int i = 0; i < moreProducts.length; i++) {
                        datas.add(moreProducts[i]);
                    }
                    view.updateView();
                    ActiveAndroid.beginTransaction();
                    try {
                        for (int i = 0; i < moreProducts.length; i++) {
                            Product item = new Product();
                            item.name = moreProducts[i].getName();
                            item.image_uri = moreProducts[i].getImage_uri();
                            item.price = moreProducts[i].getPrice();
                            item.term = query;
                            item.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                        ActiveAndroid.endTransaction();

                    } catch (Exception e) {

                    }
                }
                long size = Product.getSize(query);
                Log.d("DATA SIZE` "+query, size+"");
            }

            @Override
            public void onFailure(Call<SearchProductResultModel> call, Throwable t) {

            }
        };
    }

    public void loadMore() {
        Call<SearchProductResultModel> searchProductCall12= mService.searchPagination(query,pageNow*10+1,10);
        searchProductCall12.enqueue(mCallback);
    }

    public interface View {
        void updateView();
    }
}
