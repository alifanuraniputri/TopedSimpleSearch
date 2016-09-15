package com.alifanurani.topedsimplesearch;

import android.app.ProgressDialog;
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

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.alifanurani.topedsimplesearch.ActiveAndroidModel.Product;
import com.alifanurani.topedsimplesearch.ActiveAndroidModel.Term;
import com.alifanurani.topedsimplesearch.Adapter.ProductAdapter;
import com.alifanurani.topedsimplesearch.Utils.EndlessRecyclerViewScrollListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainPresenter.View  {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvProduct) RecyclerView mRecyclerView;

    private SearchView searchView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private ProgressDialog loading = null;
    private ProductAdapter mAdapter;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        if (mainPresenter == null)
            mainPresenter = new MainPresenter(this);
        mainPresenter.onTakeView(this);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>0) {
                    mainPresenter.setPageNow(0);
                    if (Term.getSize(query)>0) { //DB Mode
                        Log.d("SEARCH", "history");
                        mainPresenter.initSearchProductDB(query);
                        mAdapter = new ProductAdapter(mainPresenter.getDatas());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
                        mainPresenter.selectProductDB();
                        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItems) {

                                mainPresenter.setPageNow(page);
                                mainPresenter.selectProductDB();

                            }
                        });

                        loading.dismiss();
                    } else { //ALL API MODE
                        loading.show();
                        Term term = new Term();
                        term.name = query;
                        term.save();
                        mainPresenter.searchProductAPI(query);
                        mAdapter = new ProductAdapter(mainPresenter.getDatas());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
                        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItems) {
                                mainPresenter.setPageNow(page);
                                mainPresenter.loadMore();
                            }
                        });

                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateView() {
        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mainPresenter.getDatas().size());
        loading.dismiss();
    }
}
