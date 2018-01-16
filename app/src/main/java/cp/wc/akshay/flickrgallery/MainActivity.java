package cp.wc.akshay.flickrgallery;


import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cp.wc.akshay.flickrgallery.retrofitService.FlickrService;
import cp.wc.akshay.flickrgallery.model.FlickrResponse;
import cp.wc.akshay.flickrgallery.model.Photo;

import cp.wc.akshay.flickrgallery.retrofitService.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private int visibleThreshold = 5;
    int currentPage = 1;
    LinkedHashMap<String, String> map;
    FlickrService flickrService;
    ImagesAdapter adapter;
    ArrayList<Photo> photo;
    GridLayoutManager gridLayoutManager;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBundleRecyclerViewState=new Bundle();
        photo=new ArrayList<>();
        flickrService=ServiceGenerator.createService(FlickrService.class);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
        adapter=new ImagesAdapter(MainActivity.this,photo);
        adapter.setLoadMoreListener(new ImagesAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                });
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        initRequest();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    void initRequest() {
        map = new LinkedHashMap<>();
        map.put("method","flickr.interestingness.getList");
        map.put("api_key", "9f89151d82e427401680cd48dd2d5cf5");
        map.put("per_page", ""+visibleThreshold);
        map.put("page", String.valueOf(currentPage));
        map.put("format", "json");
        map.put("nojsoncallback", "1");
        Call<FlickrResponse> call = flickrService.getImages(map);
        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                try {
                    FlickrResponse response1 = response.body();
                    photo.addAll(response1.getPhotos().getPhoto());
                    adapter.notifyDataChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMore(){
        map = new LinkedHashMap<>();
        map.put("method","flickr.interestingness.getList");
        map.put("api_key", "9f89151d82e427401680cd48dd2d5cf5");
        map.put("per_page", ""+visibleThreshold);
        map.put("page", String.valueOf(currentPage));
        map.put("format", "json");
        map.put("nojsoncallback", "1");

        //add loading progress view
        photo.add(new Photo("load"));
        adapter.notifyItemInserted(photo.size()-1);

        Call<FlickrResponse> call = flickrService.getImages(map);
        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                if(response.isSuccessful()){
                    //remove loading view
                    photo.remove(photo.size()-1);
                    FlickrResponse result = response.body();
                    if(result.getPhotos().getPhoto().size()>0){
                        //add loaded data
                        photo.addAll(result.getPhotos().getPhoto());
                        currentPage++;
                    }else{//result size 0 means there is no more data available at server
                        adapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(MainActivity.this,"No More Data Available",Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                }else{
                    Log.e("MAinactivity"," Load More Response Error "+String.valueOf(response.code()));
                }
            }
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e("MAinactivity"," Load More Response Error "+t.getMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mListState=recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);

        }
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            gridLayoutManager.setSpanCount(3);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            gridLayoutManager.setSpanCount(2);

        }
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}


