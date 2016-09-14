package com.alifanurani.topedsimplesearch;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.alifanurani.topedsimplesearch.API.Api;
import com.alifanurani.topedsimplesearch.API.RestClient;
import com.alifanurani.topedsimplesearch.ActiveAndroidModel.Product;
import com.alifanurani.topedsimplesearch.ActiveAndroidModel.Term;
import com.alifanurani.topedsimplesearch.Adapter.ProductAdapter;
import com.alifanurani.topedsimplesearch.SearchModel.Data;
import com.alifanurani.topedsimplesearch.SearchModel.SearchProductResultModel;
import com.alifanurani.topedsimplesearch.Utils.EndlessRecyclerViewScrollListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    @BindView(R.id.rvProduct) RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ProgressDialog loading = null;

    private Callback<SearchProductResultModel> mCallback;
    private Api mService;
    private ProductAdapter mAdapter;
    private ArrayList<Data> datas;
    private String query;
    private int pageNow;

    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        // Create global configuration and initialize ImageLoader with this config
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        mService = RestClient.getRestAdapter().create(Api.class);
        mCallback = callbackSearch();
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredLayoutManager.setSpanCount(2);
        mAdapter = new ProductAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        if(loading == null){
            loading = new ProgressDialog(MainActivity.this);
            loading.setCancelable(false);
            loading.setMessage("Loading...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        deleteDatabase("TopedSearch.db");

        Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("TopedSearch.db")
                .setDatabaseVersion(5)
                .addModelClasses(Product.class)
                .create();
        ActiveAndroid.initialize(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>0) {
                    loading.show();
                    pageNow=0;

                    if (Term.getSize(query)>0) {
                        Log.d("SEARCH", "history");
                        searchProductDB(query);
                        loading.dismiss();
                    } else {
                        Term term = new Term();
                        term.name = query;
                        term.save();
                        searchProductAPI(query);
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }

            public void callSearch(String query) {
                //Do searching
            }

        });
        return true;
    }

    private void searchProductAPI(String q) {


        datas = new ArrayList<>();
        mAdapter = new ProductAdapter(datas);
        query = q;

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        Call<SearchProductResultModel> searchProductCall = mService.searchPagination(query,1,10);
        searchProductCall.enqueue(mCallback);
        mCallback = callbackSearch();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItems) {
                pageNow=page;
                Call<SearchProductResultModel> searchProductCall12= mService.searchPagination(query,page*10+1,10);
                searchProductCall12.enqueue(mCallback);
            }
        });

    }

    private void searchProductDB(String q) {

        datas = new ArrayList<>();
        mAdapter = new ProductAdapter(datas);
        query = q;

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        selectProductDB(0,query);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItems) {
                pageNow=page;
                selectProductDB(pageNow,query);
            }
        });

    }

    public void selectProductDB(int pageNow, String query) {
        int curSize = mAdapter.getItemCount();
        List<Product> productsDb= Product.get10(query,pageNow*10);

        if (productsDb.size()>0) {
            for (int i=0; i<productsDb.size(); i++) {
                datas.add(new Data(productsDb.get(i).name, productsDb.get(i).image_uri, productsDb.get(i).price ));
            }
        } else {
            Call<SearchProductResultModel> searchProductCall12= mService.searchPagination(query,pageNow*10+1,10);
            searchProductCall12.enqueue(mCallback);
        }


        mAdapter.notifyItemRangeInserted(curSize, datas.size());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private Callback<SearchProductResultModel> callbackSearch(){
        return new Callback<SearchProductResultModel>() {
            @Override
            public void onResponse(Call<SearchProductResultModel> call, Response<SearchProductResultModel> response) {
                if ( response.body() != null && response.body().getStatus().getError_code() == 0) {
                    Log.d("TEST", "response.body() != null && response.body().getStatus().getError_code() == 0");
                    // update the adapter, saving the last known size
                    int curSize = mAdapter.getItemCount();
                    Data[] moreProducts = response.body().getData();
                    for (int i = 0; i < moreProducts.length; i++) {
                        datas.add(moreProducts[i]);
                    }
                    mAdapter.notifyItemRangeInserted(curSize, datas.size());

                    ActiveAndroid.beginTransaction();
                    try {
                        for (int i = 0; i < moreProducts.length; i++) {
                            Product item = new Product();
                            item.name = moreProducts[i].getName();
                            item.image_uri = moreProducts[i].getImage_uri();
                            item.price = moreProducts[i].getPrice();
                            item.term = query;
                            //item.idLocal = i;
                            item.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                        ActiveAndroid.endTransaction();

                    } catch (Exception e) {

                    }

                }
                loading.dismiss();

                long size = Product.getSize(query);
                Log.d("DATA SIZE` "+query, size+"");

            }

            @Override
            public void onFailure(Call<SearchProductResultModel> call, Throwable t) {
                loading.dismiss();
            }
        };
    }
}
