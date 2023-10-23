package com.abethu.app.ui.fragment.bottomsheet;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.abethu.app.R;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.util.AppUtils;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;
import com.abethu.app.util.sharedpref.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.abethu.app.network.APIConstants.Constants;
import static com.abethu.app.network.APIConstants.Constants.MANUAL_LOGIN;
import static com.abethu.app.network.APIConstants.Params;
import static com.abethu.app.ui.activity.MainActivity.dimension;
import static com.abethu.app.util.UiUtils.animationObject;
import static com.abethu.app.util.sharedpref.Utils.getUserLoginStatus;

public class EditProfileBottomSheet extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks{

    private final String TAG = EditProfileBottomSheet.class.getSimpleName();
    private static final int PICK_IMAGE = 100;

    Unbinder unbinder;
    @BindView(R.id.userPicture)
    CircularImageView userPicture;
    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.ll_email)
    LinearLayout ll_email;
    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ll_phone)
    LinearLayout ll_phone;
    @BindView(R.id.ed_phone)
    EditText edPhone;

    @BindView(R.id.change_password)
    Button change_password;
    @BindView(R.id.social_login_text)
    TextView social_login_text;
    private int STORAGE_PERMISSION = 124;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    private Uri fileToUpload = null;


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.layout_edit_profile, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        //dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(R.color.transparentblack);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) (contentView.getParent()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        if(PrefUtils.getInstance(getActivity()).getIntValue(PrefKeys.USER_ID, -1) > 1)
            getUserLoginStatus(getActivity());

        PrefUtils prefUtils = PrefUtils.getInstance(getActivity());
        String name = prefUtils.getStringValue(PrefKeys.USER_NAME, "");
        String phone = prefUtils.getStringValue(PrefKeys.USER_MOBILE, "");
        String email = prefUtils.getStringValue(PrefKeys.USER_EMAIL, "");
        String image = prefUtils.getStringValue(PrefKeys.USER_PICTURE, "");
        String loginType = prefUtils.getStringValue(PrefKeys.LOGIN_TYPE, "");

        try {

            if(loginType.equalsIgnoreCase(MANUAL_LOGIN)) {
                ll_email.setVisibility(View.GONE);
                change_password.setVisibility(View.VISIBLE);
                social_login_text.setVisibility(View.GONE);
            }
            else {
                ll_phone.setVisibility(View.GONE);
                change_password.setVisibility(View.GONE);
                social_login_text.setVisibility(View.VISIBLE);
                social_login_text.setText(String.format("You have registered using your %s Social Account, you can't set or change password for social accounts.", loginType.toUpperCase()));
            }

            edName.setText(name);
            edName.setSelection(name.length());
            edPhone.setText(phone);
            edPhone.setEnabled(false);
            //edPhone.setSelection(phone.length());
            edEmail.setText(email);
            edEmail.setEnabled(false);
            //edEmail.setSelection(email.length());

            Glide.with(this)
                    .load(image)
                    .transition(GenericTransitionOptions.with(animationObject))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                    .into(userPicture);

            checkWidth();
        } catch (Exception e) {
            UiUtils.log(TAG,Log.getStackTraceString(e));
        }
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            EasyPermissions.requestPermissions(this, getString(R.string.needPermissionToAccessYourStoarge),
                    STORAGE_PERMISSION, Manifest.permission.READ_MEDIA_IMAGES/*,
                    Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_VIDEO*/);
        }else
        {
            EasyPermissions.requestPermissions(this, getString(R.string.needPermissionToAccessYourStoarge),
                    STORAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.userPicture, R.id.save_profile, R.id.change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userPicture:
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (Build.VERSION.SDK_INT >= 33)
                    perms = new String[] {Manifest.permission.READ_MEDIA_IMAGES/*, Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.READ_MEDIA_VIDEO*/};
                if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                    callImagePicker();
                } else {
                    getPermission();
                }
                break;
            case R.id.save_profile:
                if (validateFields()) {
                    saveProfileInBackend(fileToUpload);
                }
                break;
            case R.id.change_password:
                getDialog().dismiss();
                BottomSheetDialogFragment changePasswordBottomSheet = new ChangePasswordBottomSheet();
                changePasswordBottomSheet.show(getActivity().getSupportFragmentManager(), changePasswordBottomSheet.getTag());
                break;
        }
    }

    private void callImagePicker() {
        try {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, PICK_IMAGE);
        } catch (Exception e) {
            UiUtils.log(TAG,Log.getStackTraceString(e));
            UiUtils.showShortToast(getActivity(), getString(R.string.sorry_no_apps_to_perfrom));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            fileToUpload = data.getData();
            Glide.with(this)
                    .load(fileToUpload)
                    .transition(GenericTransitionOptions.with(animationObject))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                    .into(userPicture);
        }

    }

    private boolean validateFields() {
        if (edName.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.names_cant_be_empty));
            return false;
        }
        //Phone validation
        String phone = edPhone.getText().toString().trim();
        if (phone.length() != 0 && (phone.length() < 6 || phone.length() > 16)) {
            UiUtils.showShortToast(getActivity(), getString(R.string.phone_cant_be_stuff));
            return false;
        }
        /*if (edEmail.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getActivity(), getString(R.string.email_cant_be_empty));
            return false;
        }*/
        if (edEmail.getText().toString().trim().length() != 0 && !AppUtils.isValidEmail(edEmail.getText().toString())) {
            UiUtils.showShortToast(getActivity(), getString(R.string.enter_valid_email));
            return false;
        }
        return true;
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void saveProfileInBackend(Uri profileImageUri) {
        UiUtils.showLoadingDialog(getActivity());
        PrefUtils preferences = PrefUtils.getInstance(getActivity());

        MultipartBody.Part multipartBody = null;

        if (profileImageUri != null) {
            String filePath = getRealPathFromURIPath(profileImageUri, getActivity());
            File file = new File(filePath);

            // create RequestBody instance tempFrom file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), file);

            // MultipartBody.Part is used to send also the actual file name
            multipartBody =
                    MultipartBody.Part.createFormData(Params.PICTURE, file.getAbsolutePath(), requestFile);
        }

        Map<String, RequestBody> params = new HashMap<>();
        params.put(Params.ID, getPartFor(String.valueOf(preferences.getIntValue(PrefKeys.USER_ID, -1))));
        params.put(Params.TOKEN, getPartFor(preferences.getStringValue(PrefKeys.SESSION_TOKEN, "")));
        params.put(Params.EMAIL, getPartFor(preferences.getStringValue(PrefKeys.USER_EMAIL, "")));
        params.put(Params.NAME, getPartFor(edName.getText().toString()));
        params.put(Params.DESCRIPTION,  getPartFor(edEmail.getText().toString()));
        params.put(Params.LANGUAGE, getPartFor(preferences.getStringValue(PrefKeys.APP_LANGUAGE_STRING, "")));

        Call<String> call = apiInterface.updateUserProfile(APIConstants.APIs.UPDATE_USER_PROFILE, params, multipartBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject updateProfileResponse = null;
                try {
                    updateProfileResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (updateProfileResponse != null)
                    if (updateProfileResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        //update in sharedpref too
                        UiUtils.showShortToast(getActivity(), updateProfileResponse.optString(Params.MESSAGE));
                        prefUtils.setValue(PrefKeys.USER_NAME, edName.getText().toString());
                        prefUtils.setValue(PrefKeys.USER_EMAIL, edEmail.getText().toString());
                        prefUtils.setValue(PrefKeys.USER_PICTURE, updateProfileResponse.optString(Params.PICTURE));
                        prefUtils.setValue(PrefKeys.USER_MOBILE, edPhone.getText().toString());

                        Intent intent = new Intent("updateProfile"); //If you need extra, add: intent.putExtra("extra","something");
                        getActivity().sendBroadcast(intent);

                        dismiss();

                    } else {
                        UiUtils.showShortToast(getActivity(), updateProfileResponse.optString(Params.ERROR_MESSAGE));
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(getActivity());
            }
        });
    }

    private RequestBody getPartFor(String stuff) {
        return RequestBody.create(MediaType.parse("text/plain"), stuff);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        callImagePicker();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void checkWidth()
    {

        UiUtils.log(TAG, "Width: "+ dimension[1] + " --> widthDp: "+ dimension[3]);
        if(dimension[3]<400)
        {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll_email.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = Utils.dpToPx(56);
            ll_email.setLayoutParams(params);
            edEmail.setTextSize(14);
        }

    }

}
