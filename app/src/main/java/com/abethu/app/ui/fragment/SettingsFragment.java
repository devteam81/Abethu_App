package com.abethu.app.ui.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.facebook.login.LoginManager;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.abethu.app.R;
import com.abethu.app.model.SettingsItem;
import com.abethu.app.model.SubProfile;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.ui.activity.NewLoginActivity;
import com.abethu.app.ui.activity.PassCodeCreateActivity;
import com.abethu.app.ui.activity.PassCodeScreenActivity;
import com.abethu.app.ui.activity.PlansActivity;
import com.abethu.app.ui.activity.SupportActivity;
import com.abethu.app.ui.activity.WebViewActivity;
import com.abethu.app.ui.fragment.bottomsheet.EditProfileBottomSheet;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.ui.activity.AppSettingsActivity;
import com.abethu.app.ui.activity.MainActivity;
import com.abethu.app.ui.activity.SplashActivity;
import com.abethu.app.ui.adapter.SettingsAdapter;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefHelper;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;
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
import static com.abethu.app.network.APIConstants.Constants.FACEBOOK_LOGIN;
import static com.abethu.app.network.APIConstants.Constants.GOOGLE_LOGIN;
import static com.abethu.app.network.APIConstants.Params;
import static com.abethu.app.network.APIConstants.Params.TYPE;
import static com.abethu.app.util.UiUtils.animationObject;

public class SettingsFragment extends Fragment {

    private final String TAG = SettingsFragment.class.getSimpleName();

    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userEmail)
    TextView userEmail;
    @BindView(R.id.userPhone)
    TextView userPhone;
    @BindView(R.id.userFullPicture)
    ImageView userFullPicture;
    @BindView(R.id.userPicture)
    CircularImageView userPicture;
    @BindView(R.id.rlLoginDetails)
    RelativeLayout rlLoginDetails;
    @BindView(R.id.subscribe_btn)
    Button subscribe_btn;
    @BindView(R.id.Login_btn)
    Button login_btn;
    @BindView(R.id.llLogout)
    RelativeLayout llLogout;
    @BindView(R.id.logout_view)
    View logout_view;

    @BindView(R.id.parentalLockToggle)
    LabeledSwitch parentalLockToggle;

    @BindView(R.id.rl_settings)
    RelativeLayout rlSettings;


    boolean isPushOn = false;
    static boolean intentData = true;

    private Unbinder unbinder;
    private APIInterface apiInterface;
    private MainActivity activity;
    private SettingsAdapter settingsAdapter;
    private ArrayList<SubProfile> subProfiles = new ArrayList<>();
    private ArrayList<SettingsItem> settingItems = new ArrayList<>();

    private final BroadcastReceiver updateProfileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setUpProfileDetails();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        IntentFilter filter = new IntentFilter("updateProfile");
        getActivity().registerReceiver(updateProfileReceiver, filter);
        if(activity.getIntent().getStringExtra(TYPE)!=null &&
                activity.getIntent().getStringExtra(TYPE).equalsIgnoreCase("Login") &&
                intentData)
        {
            intentData = false;
            startActivity(new Intent(activity, NewLoginActivity.class));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Bug: in api< 23 this is never called
        // so mActivity=null
        // so app crashes with null-pointer exception
        //mContext = context;
        /*IntentFilter filter = new IntentFilter("updateProfile");
        getActivity().registerReceiver(updateProfileReceiver, filter);*/
    }


    @Override
    public void onResume() {
        super.onResume();
        //setUpSettingItems();
        setUpProfileDetails();
    }


/*    private void setUpSettingItems() {
        getSettingItems();
        settingsRecycler.setLayoutManager(new LinearLayoutManager(activity));
        settingsRecycler.setHasFixedSize(true);
        settingsAdapter = new SettingsAdapter(activity, settingItems);
        settingsRecycler.setAdapter(settingsAdapter);
        //Get notification count and update adapter in success response
        getNotificationCountFromBackendAsync();
    }*/

    private void setUpProfileDetails() {
        PrefUtils prefUtils = PrefUtils.getInstance(activity);

        checkWidth();

        if (PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
            rlLoginDetails.setVisibility(View.VISIBLE);
            subscribe_btn.setVisibility(View.VISIBLE);
            llLogout.setVisibility(View.VISIBLE);
            logout_view.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
            String name = prefUtils.getStringValue(PrefKeys.USER_NAME, "");
            String phone = prefUtils.getStringValue(PrefKeys.USER_MOBILE, "");
            String email = prefUtils.getStringValue(PrefKeys.USER_EMAIL, "");
            String image = prefUtils.getStringValue(PrefKeys.USER_PICTURE, "");

            userName.setText(name);
            userPhone.setText(phone);
            userEmail.setText(email);

            //.placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).override(70,70)
            Glide.with(this)
                    .load(image)
                    .transition(GenericTransitionOptions.with(animationObject))
                    .apply(new RequestOptions().error(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                    .into(userPicture);

            Glide.with(this)
                    .load(image)
                    .transition(GenericTransitionOptions.with(animationObject))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                    .into(userFullPicture);


            userEmail.setVisibility(email.length() == 0 ? View.GONE : View.VISIBLE);
            userPhone.setVisibility(phone.length() == 0 ? View.GONE : View.VISIBLE);
        }else {
            rlLoginDetails.setVisibility(View.GONE);
            subscribe_btn.setVisibility(View.GONE);
            llLogout.setVisibility(View.GONE);
            logout_view.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }

        isPushOn = (PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_PASS, false) &&
                !PrefUtils.getInstance(activity).getStringValue(PrefKeys.PASS_CODE, "").equalsIgnoreCase(""));
        parentalLockToggle.setOn(isPushOn);
        parentalLockToggle.setOnToggledListener((toggleableView, isOn) -> {
            if (PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_PASS, false) &&
                    !PrefUtils.getInstance(activity).getStringValue(PrefKeys.PASS_CODE, "").equalsIgnoreCase(""))
                startActivityForResult(new Intent(activity, PassCodeScreenActivity.class).putExtra("screen", true),123);
            else
                startActivity(new Intent(activity, PassCodeCreateActivity.class).putExtra("screen", false));
        });
    }

    @OnClick(R.id.userEditProfile)
    public void onEditProfile() {
        BottomSheetDialogFragment editProfileBottomSheet = new EditProfileBottomSheet();
        editProfileBottomSheet.show(activity.getSupportFragmentManager(), editProfileBottomSheet.getTag());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(updateProfileReceiver);
    }

    private void getSettingItems() {
        settingItems = new ArrayList<>();
        settingItems.add(new SettingsItem(getString(R.string.app_settings), ""
                , v -> startActivity(new Intent(activity, AppSettingsActivity.class))));
        settingItems.add(new SettingsItem(getString(R.string.log_out), "", v -> logOutConfirm()));
    }

    private void logOutConfirm() {
        new AlertDialog.Builder(activity)
                .setMessage(String.format("%s ?", getString(R.string.sure_to_text) + " " + getString(R.string.app_name)))
                .setTitle(getString(R.string.logout_confirmation))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> doLogOutUser())
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                }).create().show();
    }

    protected void doLogOutUser() {
        UiUtils.showLoadingDialog(activity);
        PrefUtils preferences = PrefUtils.getInstance(activity);
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
                    if (logoutResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.showShortToast(activity, logoutResponse.optString(Params.MESSAGE));
                        PrefUtils prefUtils = PrefUtils.getInstance(getActivity());
                        if(prefUtils.getStringValue(PrefKeys.LOGIN_TYPE, "").equalsIgnoreCase(GOOGLE_LOGIN))
                            logoutOfGmail();
                        if(prefUtils.getStringValue(PrefKeys.LOGIN_TYPE, "").equalsIgnoreCase(FACEBOOK_LOGIN))
                            logoutOfFacebook();
                        logOutUserInDevice();
                    } else {
                        UiUtils.showShortToast(activity, logoutResponse.optString(Params.ERROR_MESSAGE));
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(activity);
            }
        });
    }

    private void logoutOfGmail()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        UiUtils.log(TAG,"Logged OUT from Google");
                        UiUtils.log(TAG,"Google OUT");
                    }
                });
    }

    private void logoutOfFacebook()
    {
        LoginManager.getInstance().logOut();
        UiUtils.log(TAG,"Logged OUT from Facebook");
        UiUtils.log(TAG,"Facebook OUT");
    }

    protected void getNotificationCountFromBackendAsync() {
        PrefUtils preferences = PrefUtils.getInstance(activity);
        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID,  preferences.getIntValue(PrefKeys.USER_ID, -1));
        params.put(Params.TOKEN, preferences.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(Params.SUB_PROFILE_ID, preferences.getIntValue(PrefKeys.ACTIVE_SUB_PROFILE, 0));
        params.put(Params.LANGUAGE, preferences.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.getNotificationCount(APIConstants.APIs.NOTIFICATION_COUNT, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (isAdded()) {
                    JSONObject notificationCountResponse = null;
                    try {
                        notificationCountResponse = new JSONObject(response.body());
                    } catch (Exception e) {
                        UiUtils.log(TAG,Log.getStackTraceString(e));
                    }
                    if (notificationCountResponse != null)
                        if (notificationCountResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {

                            //get count
                            String count = notificationCountResponse.optString(Params.COUNT) + " Unread";

                            //Update count and update adapter
                            for (SettingsItem item : settingItems) {
                                if (item.getSettingName().equals(getString(R.string.notifications))) {
                                    item.setSettingSubName(count);
                                    settingsAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                NetworkUtils.onApiError(activity);
            }
        });
    }

    private void logOutUserInDevice() {
        PrefHelper.setUserLoggedOut(activity);
        MainActivity.CURRENT_FRAGMENT ="";
        Intent restartActivity = new Intent(activity, SplashActivity.class);
        restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartActivity);
        //activity.finish();
    }

    public void showLogoutPopup()
    {

        Dialog dialog = new Dialog(activity);

        dialog.setContentView(R.layout.dialog_logout_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtYes = dialog.findViewById(R.id.btnYes);
        TextView txtNo = dialog.findViewById(R.id.btnNo);

        txtNo.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        txtYes.setOnClickListener((View v) -> {
            dialog.dismiss();
            doLogOutUser();
            //logOutConfirm();
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //window.setGravity(Gravity.CENTER);
    }

    @OnClick({R.id.llParentControl, R.id.llPrivacyPolicy, R.id.llTC,  R.id.llLogout, R.id.subscribe_btn, R.id.llAppSetings, R.id.llAppSupport,R.id.llAbout, R.id.Login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llParentControl:
                if (PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_PASS, false) &&
                        !PrefUtils.getInstance(activity).getStringValue(PrefKeys.PASS_CODE, "").equalsIgnoreCase(""))
                    startActivityForResult(new Intent(activity, PassCodeScreenActivity.class).putExtra("screen", true),123);
                else
                    startActivity(new Intent(activity, PassCodeCreateActivity.class).putExtra("screen", false));
                break;
            case R.id.llPrivacyPolicy:
                startActivity(new Intent(activity, WebViewActivity.class)
                        .putExtra(WebViewActivity.PAGE_TYPE, WebViewActivity.PageTypes.PRIVACY));
                break;
            case R.id.llTC:
                startActivity(new Intent(activity, WebViewActivity.class)
                        .putExtra(WebViewActivity.PAGE_TYPE, WebViewActivity.PageTypes.TERMS));
                break;
            case R.id.llAbout:
                startActivity(new Intent(activity, WebViewActivity.class)
                        .putExtra(WebViewActivity.PAGE_TYPE, WebViewActivity.PageTypes.ABOUT));
                break;
            case R.id.llLogout:
                showLogoutPopup();
                break;
            case R.id.llAppSetings:
                startActivity(new Intent(activity, AppSettingsActivity.class));
                break;
            case R.id.llAppSupport:
                startActivity(new Intent(activity, SupportActivity.class));
                break;
            case R.id.subscribe_btn:
                startActivity(new Intent(activity, PlansActivity.class));
                break;
            case R.id.Login_btn:
                startActivity(new Intent(activity, NewLoginActivity.class));
                //startActivity(new Intent(activity, LoginActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                try {
                    if(data.getBooleanExtra("data",false)) {
                        isPushOn = PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_PASS, false);
                        parentalLockToggle.setOn(isPushOn);
                    }
                }catch (Exception e)
                {}

        }
    }

    private void checkWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        UiUtils.log(TAG, "Width: "+ width);
        UiUtils.log(TAG, "Width dp: "+ dp);

        if(dp>500 && dp<600)
        {
            int margin = (int) (50f * Resources.getSystem().getDisplayMetrics().density);
            UiUtils.log(TAG, "margin: "+ margin);

            RelativeLayout.LayoutParams head_params = (RelativeLayout.LayoutParams)rlSettings.getLayoutParams();
            head_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlSettings.setLayoutParams(head_params);

            LinearLayout.LayoutParams login_detail_params = (LinearLayout.LayoutParams)rlLoginDetails.getLayoutParams();
            login_detail_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlLoginDetails.setLayoutParams(login_detail_params);

            LinearLayout.LayoutParams login_params = (LinearLayout.LayoutParams)login_btn.getLayoutParams();
            login_params.setMargins(margin, 200, margin, 200); //substitute parameters for left, top, right, bottom
            login_btn.setLayoutParams(login_params);
        }else if(dp>600 && dp<700)
        {
            int margin = (int) (80f * Resources.getSystem().getDisplayMetrics().density);
            UiUtils.log(TAG, "margin: "+ margin);

            LinearLayout.LayoutParams head_params = (LinearLayout.LayoutParams)rlSettings.getLayoutParams();
            head_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlSettings.setLayoutParams(head_params);

            LinearLayout.LayoutParams login_detail_params = (LinearLayout.LayoutParams)rlLoginDetails.getLayoutParams();
            login_detail_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlLoginDetails.setLayoutParams(login_detail_params);

            LinearLayout.LayoutParams login_params = (LinearLayout.LayoutParams)login_btn.getLayoutParams();
            login_params.setMargins(margin, 220, margin, 220); //substitute parameters for left, top, right, bottom
            login_btn.setLayoutParams(login_params);
        }else if(dp>700 && dp<800)
        {
            int margin = (int) (100f * Resources.getSystem().getDisplayMetrics().density);
            UiUtils.log(TAG, "margin: "+ margin);

            RelativeLayout.LayoutParams head_params = (RelativeLayout.LayoutParams)rlSettings.getLayoutParams();
            head_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlSettings.setLayoutParams(head_params);

            LinearLayout.LayoutParams login_detail_params = (LinearLayout.LayoutParams)rlLoginDetails.getLayoutParams();
            login_detail_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlLoginDetails.setLayoutParams(login_detail_params);

            LinearLayout.LayoutParams login_params = (LinearLayout.LayoutParams)login_btn.getLayoutParams();
            login_params.setMargins(margin, 250, margin, 250); //substitute parameters for left, top, right, bottom
            login_btn.setLayoutParams(login_params);
        }else if(dp>800)
        {
            int margin = (int) (150f * Resources.getSystem().getDisplayMetrics().density);
            UiUtils.log(TAG, "margin(800): "+ margin);

            LinearLayout.LayoutParams head_params = (LinearLayout.LayoutParams)rlSettings.getLayoutParams();
            head_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlSettings.setLayoutParams(head_params);

            LinearLayout.LayoutParams login_detail_params = (LinearLayout.LayoutParams)rlLoginDetails.getLayoutParams();
            login_detail_params.setMargins(margin, 0, margin, 0); //substitute parameters for left, top, right, bottom
            rlLoginDetails.setLayoutParams(login_detail_params);

            LinearLayout.LayoutParams login_params = (LinearLayout.LayoutParams)login_btn.getLayoutParams();
            login_params.setMargins(margin, 300, margin, 300); //substitute parameters for left, top, right, bottom
            login_btn.setLayoutParams(login_params);
        }
    }
}
