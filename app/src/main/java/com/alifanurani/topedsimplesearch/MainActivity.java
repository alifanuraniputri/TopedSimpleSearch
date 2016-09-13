package com.alifanurani.topedsimplesearch;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private Call<SearchProductResultModel[]> mSearchCall;
    private Callback<SearchProductResultModel[]> mCallback;
    private Api mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = RestClient.getRestAdapter().create(Api.class);

        mCallback = callbackSearch();

       /* mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItems) {
                String token = null;
                if(SessionManager.isLogin(getContext())){
                    token = SessionManager.getPreferences(getContext(), Constants.SHARED_PREFERENCES_KEY_TOKEN);
                }
                Call<ListAccountModel> mAccountCall = mAccountService.listAccount(token, "no-cache", page);
                mAccountCall.enqueue(mListAccountCallback);
            }
        });*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProduct(query);
                return true;
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

    private void searchProduct(String query) {
        if (mSearchCall !=null ) {
            mSearchCall.cancel();
        }
        mSearchCall = mService.search(query);
        mSearchCall.enqueue(mCallback);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    private Callback<SearchProductResultModel[]> callbackSearch(){
        return new Callback<SearchProductResultModel[]>() {
            @Override
            public void onResponse(Call<SearchProductResultModel[]> call, Response<SearchProductResultModel[]> response) {
/*                if (response.isSuccess()) {
                    mOASearchResult = response.body();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        // Cursor
                        String[] columns = new String[] { "_id", "text" };
                        Object[] temp = new Object[] { 0, "default" };
                        MatrixCursor cursor = new MatrixCursor(columns);
                        List<String> results = new ArrayList<>();
                        for(int i = 0; i < response.body().length; i++) {
                            temp[0] = i;
                            temp[1] = response.body()[i].getNama();
                            cursor.addRow(temp);
                            results.add(response.body()[i].getNama());
                        }

                        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                            @Override
                            public boolean onSuggestionSelect(int i) {

                                return true;
                            }

                            @Override
                            public boolean onSuggestionClick(int i) {
                                Cursor cursor = (Cursor)     searchView.getSuggestionsAdapter().getItem(i);
                                String feedName = cursor.getString(1);
                                searchView.setQuery(feedName, false);
                                searchView.clearFocus();

                                for (SearchProductResultModel list : mOASearchResult) {
                                    String nama = list.getNama();
                                    if (nama.equals(feedName)) {
                                        selectedIdSearch = list.getId();
                                        selectedType = list.getType();
                                        break;
                                    }
                                }

                                final Intent intent;
                                if (selectedType.equals("penyelenggara")) {
                                    intent = new Intent(getContext(), LembagaActivity.class);
                                    intent.putExtra("penyelenggaraId", selectedIdSearch);
                                } else {
                                    intent = new Intent(getContext(), PembicaraActivity.class);
                                    intent.putExtra("pembicaraId", selectedIdSearch);
                                }
                                getContext().startActivity(intent);

                                return true;
                            }
                        });

                        searchAcaraAdapter = new SearchAcaraAdapter(getContext(), cursor, results);
                        searchView.setSuggestionsAdapter(searchAcaraAdapter);
                        if (searchAcaraAdapter !=null) {
                            searchAcaraAdapter.notifyDataSetChanged();
                        }
                    }


                }*/
            }

            @Override
            public void onFailure(Call<SearchProductResultModel[]> call, Throwable t) {

            }
        };
    }
}
