package com.alifanurani.topedsimplesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ProgressDialog loading = null;

    private Callback<SearchProductResultModel> mCallback;
    private Api mService;
    private ProductAdapter mAdapter;
    private ArrayList<Data> datas;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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


        mRecyclerView = (RecyclerView) findViewById(R.id.rvProduct);
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
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loading.show();
                searchProduct(query);
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

    private void searchProduct(String q) {


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
                Call<SearchProductResultModel> searchProductCall12= mService.searchPagination(query,page*10+1,10);
                searchProductCall12.enqueue(mCallback);
            }
        });

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


                    // for efficiency purposes, only notify the adapter of what elements that got changed
                    // curSize will equal to the index of the first element inserted because the list is 0-indexed
                    mAdapter.notifyItemRangeInserted(curSize, datas.size());

                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<SearchProductResultModel> call, Throwable t) {
                loading.dismiss();
            }
        };
    }
}
