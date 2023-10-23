package com.abethu.app.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abethu.app.R;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abethu.app.network.APIConstants.*;

public class TestNetworkActivity extends BaseActivity {

    private final String TAG = TestNetworkActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /*@BindView(R.id.our_server)
    TextView ourServer;
    @BindView(R.id.internet_server)
    TextView internetServer;
    @BindView(R.id.testDisplayArea)
    LinearLayout testDisplayArea;*/
    @BindView(R.id.checkNetworkImage)
    ImageView checkNetworkImage;
    @BindView(R.id.testStatus)
    TextView testStatus;
    @BindView(R.id.errorDescription)
    TextView errorDescription;
    @BindView(R.id.testingProgress)
    ProgressBar testingProgress;
    @BindView(R.id.testBtn)
    Button testBtn;

    Drawable successDrawable;
    Drawable failedDrawable;
    Drawable emptyDrawable;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_network);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        apiInterface = APIClient.getClient().create(APIInterface.class);

        successDrawable = getResources().getDrawable(R.drawable.ic_tick_mark);
        failedDrawable = getResources().getDrawable(R.drawable.ic_cross);
        emptyDrawable = getResources().getDrawable(android.R.color.transparent);

        //testDisplayArea.setVisibility(View.GONE);
        testingProgress.setVisibility(View.INVISIBLE);
        checkNetworkImage.setVisibility(View.VISIBLE);

        testStatus.setVisibility(View.VISIBLE);
        testStatus.setText(getString(R.string.check_your_network_title));
        errorDescription.setVisibility(View.VISIBLE);
        errorDescription.setText(String.format(getString(R.string.test_failed_description)));
    }


    @OnClick(R.id.testBtn)
    public void onViewClicked() {
        //testDisplayArea.setVisibility(View.VISIBLE);
        testingProgress.setVisibility(View.VISIBLE);
        //checkNetworkImage.setVisibility(View.GONE);
        //ourServer.setCompoundDrawablesRelativeWithIntrinsicBounds(emptyDrawable, null, null, null);
        //internetServer.setCompoundDrawablesRelativeWithIntrinsicBounds(emptyDrawable, null, null, null);

        PrefUtils preferences = PrefUtils.getInstance(this);
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, preferences.getIntValue(PrefKeys.USER_ID, 1));
        params.put(APIConstants.Params.TOKEN, preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""));

        Call<String> call = apiInterface.getAppConfigs(APIConstants.APIs.GET_APP_CONFIG, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                testingProgress.setVisibility(View.GONE);
                testBtn.setText(getString(R.string.test_again));
                JSONObject networkCheckResponse = null;
                try {
                    networkCheckResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (networkCheckResponse != null) {
                    if (networkCheckResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        updateViews(false, "Network Check Successful");
                        checkNetworkImage.setImageDrawable(successDrawable);
                        //ourServer.setCompoundDrawablesRelativeWithIntrinsicBounds(successDrawable, null, null, null);
                        //internetServer.setCompoundDrawablesRelativeWithIntrinsicBounds(successDrawable, null, null, null);
                    } else {
                        updateViews(false, "Network Check Successful");
                        checkNetworkImage.setImageDrawable(successDrawable);
                       // ourServer.setCompoundDrawablesRelativeWithIntrinsicBounds(failedDrawable, null, null, null);
                        //internetServer.setCompoundDrawablesRelativeWithIntrinsicBounds(successDrawable, null, null, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                testingProgress.setVisibility(View.GONE);
                updateViews(true, "Network Test Failed");
                checkNetworkImage.setImageDrawable(failedDrawable);
                //ourServer.setCompoundDrawablesRelativeWithIntrinsicBounds(failedDrawable, null, null, null);
                //internetServer.setCompoundDrawablesRelativeWithIntrinsicBounds(failedDrawable, null, null, null);
            }
        });
    }

    private void updateViews(boolean isSuccess, String message) {
        //errorDescription.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        errorDescription.setTextColor(isSuccess ? getResources().getColor(R.color.cross) :getResources().getColor(R.color.tick));
        //testStatus.setText(isSuccess ? getString(R.string.network_check_success) : getString(R.string.network_check_failed));
        testStatus.setText(getString(R.string.internet_connection));
        //if (!isSuccess)
            errorDescription.setText(message);
    }
}
