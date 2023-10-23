package com.abethu.app.ui.fragment.bottomsheet;

import android.app.Dialog;
import android.content.Intent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.abethu.app.R;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.ui.activity.SplashActivity;
import com.abethu.app.ui.adapter.OfflineVideoAdapter;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefHelper;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abethu.app.network.APIConstants.Constants;
import static com.abethu.app.network.APIConstants.Params;
import static com.abethu.app.util.sharedpref.Utils.getUserLoginStatus;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private final String TAG = ChangePasswordBottomSheet.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.ed_old_password)
    EditText edOldPassword;
    @BindView(R.id.ed_new_password)
    EditText edNewPassword;
    @BindView(R.id.ed_confirm_password)
    EditText edConfirmPassword;
    @BindView(R.id.submit_btn)
    Button submitBtn;

    APIInterface apiInterface;


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.layout_change_password, null);
        unbinder = ButterKnife.bind(this, contentView);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        dialog.setContentView(contentView);
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(R.color.transparentblack);

        if(PrefUtils.getInstance(getActivity()).getIntValue(PrefKeys.USER_ID, -1) > 1)
            getUserLoginStatus(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.img_old_password, R.id.img_new_password, R.id.img_re_password})
    public void showHidePass(View view){

        if(view.getId()==R.id.img_old_password){

            if(edOldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_show);
                //Show Password
                edOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_hide);
                //Hide Password
                edOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }else if(view.getId()==R.id.img_new_password){

            if(edNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_show);
                //Show Password
                edNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_hide);
                //Hide Password
                edNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }else if(view.getId()==R.id.img_re_password){

            if(edConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_show);
                //Show Password
                edConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_eye_hide);
                //Hide Password
                edConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    @OnClick(R.id.submit_btn)
    protected void changePasswordClicked() {
        if (validated()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.change_pass))
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        changePasswordInBackend();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel())
                    .create().show();
        }
    }

    private boolean validated() {
        if (edOldPassword.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.password_cant_be_empty));
            return false;
        }
        if (edNewPassword.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.password_cant_be_empty));
            return false;
        }
        if (edConfirmPassword.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.password_cant_be_empty));
            return false;
        }
        if (!edNewPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
            UiUtils.showShortToast(getActivity(), getString(R.string.password_not_match));
            return false;
        }
        return true;
    }

    private void changePasswordInBackend() {
        UiUtils.showLoadingDialog(getActivity());
        PrefUtils prefUtils = PrefUtils.getInstance(getActivity());
        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, prefUtils.getIntValue(PrefKeys.USER_ID, -1));
        params.put(Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(Params.OLD_PASSWORD, edOldPassword.getText().toString());
        params.put(Params.PASSWORD, edNewPassword.getText().toString());
        params.put(Params.CONFIRM_PASSWORD, edConfirmPassword.getText().toString());
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.changePassword(APIConstants.APIs.CHANGE_PASSWORD, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject changePasswordResponse = null;
                try {
                    changePasswordResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (changePasswordResponse != null) {
                    if (changePasswordResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.showShortToast(getActivity(), changePasswordResponse.optString(Params.MESSAGE));
                        doLogOutUser();
                    } else {
                        UiUtils.showShortToast(getActivity(), changePasswordResponse.optString(Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(getActivity());
            }
        });
    }

    protected void doLogOutUser() {
        UiUtils.showLoadingDialog(getActivity());
        PrefUtils preferences = PrefUtils.getInstance(getActivity());
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, preferences.getIntValue(PrefKeys.USER_ID, -1));
        params.put(APIConstants.Params.TOKEN, preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(APIConstants.Params.LANGUAGE, preferences.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.logOutUser(APIConstants.APIs.LOGOUT,params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject logoutResponse = null;
                try {
                    logoutResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (logoutResponse != null)
                    if (logoutResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.showLongToast(getActivity(), getString(R.string.login_again_with_new_password));
                        logOutUserInDevice();
                    } else {
                        UiUtils.showShortToast(getActivity(), logoutResponse.optString(APIConstants.Params.ERROR_MESSAGE));
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(getActivity());
            }
        });
    }

    private void logOutUserInDevice() {
        PrefHelper.setUserLoggedOut(getActivity());
        Intent restartActivity = new Intent(getActivity(), SplashActivity.class);
        restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartActivity);
        getActivity().finish();
    }
}
