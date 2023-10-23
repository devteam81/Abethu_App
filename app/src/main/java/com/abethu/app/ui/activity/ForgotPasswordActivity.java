package com.abethu.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.abethu.app.R;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefKeys;

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

import static com.abethu.app.network.APIConstants.Constants;
import static com.abethu.app.network.APIConstants.Params;

public class ForgotPasswordActivity extends BaseActivity {

    private final String TAG = ForgotPasswordActivity.class.getSimpleName();
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.ed_phone)
    EditText edPhone;

    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_forgot_pass);
        ButterKnife.bind(this);
        back_btn.setOnClickListener(v -> onBackPressed());
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @OnClick({R.id.submit_btn/*, R.id.back_btn*/})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit_btn:
                if (validateFields()) {
                    sendConfirmationMail();
                }
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private boolean validateFields() {
        //Phone validation
        if(edPhone.getText().toString().trim().length()==0){
            UiUtils.showShortToast(ForgotPasswordActivity.this,getString(R.string.enter_phone_no));
            return false;
        }
        String phone = edPhone.getText().toString().trim();
        if (phone.length() != 0 && (phone.length() < 6 || phone.length() > 16)) {
            UiUtils.showShortToast(ForgotPasswordActivity.this, getString(R.string.phone_cant_be_stuff));
            return false;
        }
        return true;
    }

    private void sendConfirmationMail() {
        UiUtils.showLoadingDialog(this);
        Map<String, String> params = new HashMap<>();
        params.put(Params.MOBILE, edPhone.getText().toString());
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.forgotPassword(APIConstants.APIs.FORGOT_PASSWORD, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject forgotPasswordResponse = null;
                try {
                    forgotPasswordResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (forgotPasswordResponse != null) {
                    if (forgotPasswordResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        //UiUtils.showShortToast(ForgotPasswordActivity.this, forgotPasswordResponse.optString(Params.MESSAGE));
                        Intent i = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                        i.putExtra(Params.ID, forgotPasswordResponse.optString(Params.ID));
                        i.putExtra(Params.MOBILE, edPhone.getText().toString());
                        UiUtils.log("TAG","OTP: " + forgotPasswordResponse.optString(Params.OTP.toLowerCase()));
                        i.putExtra(Params.OTP, forgotPasswordResponse.optString(Params.OTP.toLowerCase()));
                        startActivity(i);
                        finish();
                    } else {
                        UiUtils.showShortToast(ForgotPasswordActivity.this, forgotPasswordResponse.optString(Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(ForgotPasswordActivity.this);
            }
        });
    }
}
