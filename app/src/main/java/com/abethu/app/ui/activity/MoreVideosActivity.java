package com.abethu.app.ui.activity;

import static com.abethu.app.network.APIConstants.APIs.CLEAR_SPAM_LIST;
import static com.abethu.app.network.APIConstants.APIs.SEE_ALL_URL;
import static com.abethu.app.network.APIConstants.Params.VERSION_CODE;
import static com.abethu.app.ui.activity.MainActivity.CURRENT_IP;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.abethu.app.BuildConfig;
import com.abethu.app.R;
import com.abethu.app.listener.OnLoadMoreVideosListener;
import com.abethu.app.model.Video;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.ui.adapter.MoreVideosAdapter;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreVideosActivity extends BaseActivity implements OnLoadMoreVideosListener, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = MoreVideosActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.moreVideosRecycler)
    RecyclerView moreVideosRecycler;
    @BindView(R.id.noResultLayout)
    TextView noResultLayout;
    MoreVideosAdapter moreVideosAdapter;
    ArrayList<Video> moreVideos = new ArrayList<Video>();
    APIInterface apiInterface;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    Intent intent;
    String urlType = "";
    String pageType = "";
    String title = "";
    private String categoryId;
    private int subCategoryId;
    private int castNCrewId;
    private int genreId;
    private String pageUrlId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_more_videos);
        ButterKnife.bind(this);
        swipe.setOnRefreshListener(this);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        intent = getIntent();
        setUpAdapter();
    }

    public void setUpAdapter() {
        moreVideosAdapter = new MoreVideosAdapter(MoreVideosActivity.this, moreVideos);
        moreVideosAdapter.setOnLoadMoreVideosListener(this);
        moreVideosRecycler.setHasFixedSize(true);
        moreVideosRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        moreVideosRecycler.setItemAnimator(new DefaultItemAnimator());
        moreVideosRecycler.setAdapter(moreVideosAdapter);
        moreVideosRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (llmanager.findLastCompletelyVisibleItemPosition() == (moreVideosAdapter.getItemCount() - 1)) {
                    moreVideosAdapter.showLoading();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(intent.getExtras() != null)
        {
            urlType = intent.getStringExtra(APIConstants.Params.URL_TYPE);
            pageType = intent.getStringExtra(APIConstants.Params.PAGE_TYPE);
            pageUrlId = intent.getStringExtra(APIConstants.Params.URL_TYPE_ID);
            categoryId = intent.getStringExtra(APIConstants.Params.CATEGORY_ID);
            subCategoryId = intent.getIntExtra(APIConstants.Params.SUB_CATEGORY_ID, 0);
            castNCrewId = intent.getIntExtra(APIConstants.Params.CAST_CREW_ID, 0);
            genreId = intent.getIntExtra(APIConstants.Params.GENRE_ID, 0);
            UiUtils.log(TAG,"Value--> "+ genreId);
            title = intent.getStringExtra(APIConstants.Params.TITLE);
            toolbar.setTitle(title);

            getMoreVideosFromBackend(urlType,pageType,pageUrlId,categoryId,subCategoryId,castNCrewId,genreId,0);
        }
    }

    protected void getMoreVideosFromBackend(String urlType, String pageType,String urlPageId, String categoryId, int subCateId,
                                            int castNCrewId, int genreId, int skip) {
        PrefUtils prefUtils = PrefUtils.getInstance(MoreVideosActivity.this);

        UiUtils.showLoadingDialog(MoreVideosActivity.this);
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, prefUtils.getIntValue(PrefKeys.USER_ID, 1));
        params.put(APIConstants.Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(APIConstants.Params.SUB_PROFILE_ID, prefUtils.getIntValue(PrefKeys.ACTIVE_SUB_PROFILE, 0));
        params.put(APIConstants.Params.URL_TYPE, urlType);
        params.put(APIConstants.Params.PAGE_TYPE, pageType);
        params.put(APIConstants.Params.URL_TYPE_ID, urlPageId);
        params.put(APIConstants.Params.CATEGORY_ID, categoryId);
        params.put(APIConstants.Params.SUB_CATEGORY_ID, subCateId);
        params.put(APIConstants.Params.CAST_CREW_ID, castNCrewId);
        params.put(APIConstants.Params.GENRE_ID, genreId);
        params.put(APIConstants.Params.SKIP, skip);
        params.put(APIConstants.Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));
        params.put(APIConstants.Params.IP, CURRENT_IP);
        params.put(APIConstants.Params.APPVERSION, BuildConfig.VERSION_NAME);
        params.put(VERSION_CODE, BuildConfig.VERSION_CODE);

        Call<String> call = apiInterface.moreVideosList(SEE_ALL_URL, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (skip == 0) moreVideos.clear();
                if (swipe.isRefreshing()) swipe.setRefreshing(false);
                UiUtils.hideLoadingDialog();
                if (skip != 0) {
                    moreVideosAdapter.dismissLoading();
                }
                JSONObject moreVideosResponse = null;
                try {
                    moreVideosResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (moreVideosResponse != null) {
                    if (moreVideosResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONArray wishListArray = moreVideosResponse.optJSONArray(APIConstants.Params.DATA);
                        for (int i = 0; i < wishListArray.length(); i++) {
                            JSONObject object = wishListArray.optJSONObject(i);
                            Video video = new Video();
                            video.setAdminVideoId(object.optInt(APIConstants.Params.ADMIN_VIDEO_ID));
                            video.setTitle(object.optString(APIConstants.Params.TITLE));
                            video.setCategoryId(object.optInt(APIConstants.Params.CATEGORY_ID));
                            video.setGenreId(object.optInt(APIConstants.Params.GENRE_ID));
                            video.setThumbNailUrl(object.optString(APIConstants.Params.MOBILE_IMAGE));
                            video.setBrowseImage(object.optString(APIConstants.Params.BROWSE_IMAGE));
                            video.setA_record(object.optInt(APIConstants.Params.A_RECORD));
                            moreVideos.add(video);
                        }

                        onDataChanged();
                    } else {
                        UiUtils.showShortToast(MoreVideosActivity.this, moreVideosResponse.optString(APIConstants.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                NetworkUtils.onApiError(MoreVideosActivity.this);
            }
        });
    }

    private void onDataChanged() {
        moreVideosAdapter.notifyDataSetChanged();
        if (moreVideos.isEmpty()) {
            noResultLayout.setVisibility(View.VISIBLE);
            moreVideosRecycler.setVisibility(View.GONE);
        } else {
            noResultLayout.setVisibility(View.GONE);
            moreVideosRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        onResume();
    }

    @Override
    public void onLoadMore(int skip) {
        moreVideosAdapter.showLoading();
        getMoreVideosFromBackend(urlType,pageType,pageUrlId,categoryId,subCategoryId,castNCrewId,genreId,skip);
    }
}
