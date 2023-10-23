package com.abethu.app.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abethu.app.R;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.ui.fragment.LoginFragment;
import com.abethu.app.ui.fragment.SearchFragment;
import com.abethu.app.ui.fragment.SignupFragment;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.sharedpref.PrefHelper;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;
import com.abethu.app.util.sharedpref.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abethu.app.network.APIConstants.APIs.USER_SUBSCRIPTION;
import static com.abethu.app.network.APIConstants.Params.DISTRIBUTOR;
import static com.abethu.app.network.APIConstants.Params.VERSION_CODE;
import static com.abethu.app.ui.activity.MainActivity.CURRENT_IP;
import static com.abethu.app.util.Fragments.HOME_FRAGMENTS;
import static com.abethu.app.util.Fragments.LOGIN_FRAGMENTS;

public class NewLoginActivity extends BaseActivity{

    private final String TAG = NewLoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;

    private static String CURRENT_FRAGMENT ;
    private Fragment fragment;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvSignup)
    TextView tvSignup;
    @BindView(R.id.ll_reg_log)
    RelativeLayout ll_reg_log;

    @BindView(R.id.txtGoogle)
    TextView txtGoogle;
    @BindView(R.id.txtFacebook)
    TextView txtFacebook;

    private APIInterface apiInterface;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    private PrefUtils prefUtils;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        ButterKnife.bind(this);
        replaceFragmentWithAnimation(new SignupFragment(), LOGIN_FRAGMENTS[0], false);

        back_btn.setOnClickListener(v -> onBackPressed());

        apiInterface = APIClient.getClient().create(APIInterface.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        //Fb setup
        callbackManager = CallbackManager.Factory.create();

        //Google setup
        mGoogleSignInClient = GoogleSignIn.getClient(NewLoginActivity.this, gso);
        FirebaseAuth.getInstance().signOut();
        mAuth = FirebaseAuth.getInstance();

        prefUtils = PrefUtils.getInstance(this);

        checkWidth();
    }


    public void replaceFragmentWithAnimation(Fragment fragment, String tag, boolean toBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        CURRENT_FRAGMENT = tag;
        this.fragment = fragment;
        if (toBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.replace(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
    }

    @OnClick(R.id.tvSignup)
    public void onSignUpClick(){
        tvSignup.setBackgroundResource(R.drawable.bg_nav_selected);
        //tvSignup.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        tvSignup.setPadding(44,28,44,28);
        tvSignup.setTextColor(ContextCompat.getColor(this,R.color.white));
        tvLogin.setBackgroundColor(Color.TRANSPARENT);
        tvLogin.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryHeader));
        ll_reg_log.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.bg_registeration,null));

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if(f instanceof SignupFragment){
            return;
        }
        replaceFragmentWithAnimation(new SignupFragment(), LOGIN_FRAGMENTS[1], false);
    }

    @OnClick(R.id.tvLogin)
    public void onLoginClick(){
        tvLogin.setBackgroundResource(R.drawable.bg_nav_selected);
        //tvLogin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        tvLogin.setPadding(44,28,44,28);
        tvLogin.setTextColor(ContextCompat.getColor(this,R.color.white));
        tvSignup.setBackgroundColor(Color.TRANSPARENT);
        tvSignup.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryHeader));
        ll_reg_log.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.bg_login,null));

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if(f instanceof LoginFragment){
            return;
        }
        replaceFragmentWithAnimation(new LoginFragment(), LOGIN_FRAGMENTS[1], false);
    }

    //Social Logins
    @OnClick({R.id.ll_google_sign, R.id.ll_facebook_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_google_sign:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.ll_facebook_sign:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"/*, "user_friends"*/));
                // LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
                                UiUtils.log(TAG,"Object--> "+object);
                                doSocialLoginUser(object.optString("id")
                                        , object.optString("email")
                                        , object.optString("name")
                                        , "https://graph.facebook.com/" + object.optString("id") + "/picture?type=large"
                                        , APIConstants.Constants.FACEBOOK_LOGIN);
                            });
                            Bundle params = new Bundle();
                            params.putString("fields", "id,name,link,email,picture");
                            request.setParameters(params);
                            request.executeAsync();
                        }
                        UiUtils.log(TAG, "onSuccess: ");
                        //handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        UiUtils.log(TAG, "onCancel: ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        UiUtils.log(TAG, "onError: "+error.getLocalizedMessage());
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                UiUtils.log(TAG,"After Selection");
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    UiUtils.log(TAG,task.toString());
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    UiUtils.log(TAG,account.getDisplayName());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    // ...
                    UiUtils.log(TAG,e.getMessage());
                    UiUtils.showShortToast(NewLoginActivity.this, getString(R.string.login_cancelled));
                }
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void handleSignInResult(FirebaseUser firebaseUser) {
        try {
            String photoImg;
            try {
                photoImg = firebaseUser.getPhotoUrl().toString();
            } catch (Exception e) {
                UiUtils.log(TAG, "handleSignInResult: photo "+e.getMessage());
                e.printStackTrace();
                photoImg = "";
            }
            doSocialLoginUser(firebaseUser.getUid()
                    , firebaseUser.getEmail()
                    , firebaseUser.getDisplayName()
                    , photoImg
                    , APIConstants.Constants.GOOGLE_LOGIN);
        } catch (Exception e) {
            UiUtils.log(TAG, "handleSignInResult: doSocialLoginUser "+e.getMessage());
            UiUtils.showShortToast(NewLoginActivity.this, getString(R.string.login_cancelled));
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(NewLoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        UiUtils.log(TAG,"URL--> "+ user.getPhotoUrl());
                        handleSignInResult(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        UiUtils.showShortToast(NewLoginActivity.this, getString(R.string.login_cancelled));
                    }
                    task.addOnFailureListener(e -> {
                    });
                    // ...
                });
    }

    protected void doSocialLoginUser(String socialUniqueId, String email, String name, String picture, String loginBy) {
        try {

            UiUtils.showLoadingDialog(NewLoginActivity.this);
            HashMap<String,String> deviceDetails =  Utils.getDeviceDetails(NewLoginActivity.this);
            Map<String, String> params = new HashMap<>();
            params.put(APIConstants.Params.SOCIAL_UNIQUE_ID, socialUniqueId);
            params.put(APIConstants.Params.LOGIN_BY, loginBy);
            params.put(APIConstants.Params.EMAIL, email);
            params.put(APIConstants.Params.NAME, name);
            params.put(APIConstants.Params.MOBILE,"");
            params.put(APIConstants.Params.PICTURE, picture);
            params.put(APIConstants.Params.DEVICE_TYPE, APIConstants.Constants.ANDROID);
            params.put(APIConstants.Params.DEVICE_TOKEN, prefUtils.getStringValue(PrefKeys.FCM_TOKEN,""));
            params.put(APIConstants.Params.TIME_ZONE, TimeZone.getDefault().getID());
            params.put(APIConstants.Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));
            params.put(APIConstants.Params.PHONEDETAILS, new JSONObject(deviceDetails).toString());
            params.put(APIConstants.Params.APPVERSION, deviceDetails.get(APIConstants.Params.APPVERSION));
            params.put(VERSION_CODE, deviceDetails.get(VERSION_CODE));
            params.put(APIConstants.Params.DEVICE, deviceDetails.get(APIConstants.Params.DEVICE));
            params.put(APIConstants.Params.BEBUEXT, deviceDetails.get(APIConstants.Params.BEBUEXT));
            params.put(APIConstants.Params.BRAND, deviceDetails.get(APIConstants.Params.BRAND));
            params.put(APIConstants.Params.MODEL, deviceDetails.get(APIConstants.Params.MODEL));
            params.put(APIConstants.Params.VERSION, deviceDetails.get(APIConstants.Params.VERSION));
            params.put(APIConstants.Params.PLAT, deviceDetails.get(APIConstants.Params.PLAT));
            params.put(APIConstants.Params.IP, CURRENT_IP);
            params.put(DISTRIBUTOR, APIConstants.Constants.DISTRIBUTOR);

            Call<String> call = apiInterface.socialLoginUser(APIConstants.APIs.SOCIAL_LOGIN, params);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    UiUtils.hideLoadingDialog();
                    if(response==null){
                        UiUtils.showShortToast(NewLoginActivity.this, getString(R.string.login_cancelled));
                        return;
                    }
                    JSONObject socialLoginResponse = null;
                    try {
                        socialLoginResponse = new JSONObject(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (socialLoginResponse != null) {
                        if (socialLoginResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                            UiUtils.showShortToast(NewLoginActivity.this, getString(R.string.welcome) + name + "!");
                            loginUserInDevice(socialLoginResponse, loginBy);
                        } else {
                            UiUtils.showShortToast(NewLoginActivity.this, socialLoginResponse.optString(APIConstants.Params.ERROR_MESSAGE));
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    UiUtils.hideLoadingDialog();
                    NetworkUtils.onApiError(NewLoginActivity.this);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
            UiUtils.log(TAG,"ERRPR--> "+ e.getMessage());
        }
    }

    private void loginUserInDevice(JSONObject data, String loginBy) {
        PrefHelper.setUserLoggedIn(NewLoginActivity.this, data.optInt(APIConstants.Params.USER_ID)
                , data.optString(APIConstants.Params.TOKEN)
                , data.optString(APIConstants.Params.LOGIN_TOKEN)
                , loginBy
                , data.optString(APIConstants.Params.EMAIL)
                , data.optString(APIConstants.Params.NAME)
                , data.optString(APIConstants.Params.MOBILE)
                , data.optString(APIConstants.Params.DESCRIPTION)
                , data.optString(APIConstants.Params.PICTURE)
                , data.optString(APIConstants.Params.AGE)
                , data.optString(APIConstants.Params.NOTIF_PUSH_STATUS)
                , data.optString(APIConstants.Params.NOTIF_PUSH_STATUS)
                , data.optString(APIConstants.Params.DEFAULT_EMAIL)
                , data.optString(APIConstants.Params.DEFAULT_MOBILE));
        getSubscriptionStatus();
    /*    Intent toHome = new Intent(activity, MainActivity.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);*/
        finish();
    }

    private void checkWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        UiUtils.log(TAG, "Width: "+ width);
        UiUtils.log(TAG, "Width dp: "+ dp);

        if(dp<400)
        {
            txtGoogle.setTextSize(16f);
            txtFacebook.setTextSize(16f);
        }
        /*if(dp>500 && dp<600)
        {
            binding.txtGoogle.setTextSize(18f);

        }else if(dp>600 && dp<700)
        {
            binding.txtGoogle.setTextSize(18f);
        }else if(dp>700 && dp<800)
        {
            binding.txtGoogle.setTextSize(18f);
        }else if(dp>800)
        {
            binding.txtGoogle.setTextSize(18f);
        }*/
    }

    private void getSubscriptionStatus()
    {
        UiUtils.log(TAG,"API Called");
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, prefUtils.getIntValue(PrefKeys.USER_ID, -1));
        params.put(APIConstants.Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(APIConstants.Params.IP, CURRENT_IP);

        Call<String> call = apiInterface.getSubscriptionStatus(USER_SUBSCRIPTION, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject subscriptionStatusResponse = null;
                try {
                    subscriptionStatusResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                /*if (subscriptionStatusResponse != null) {
                    supportArrayList = parseSupportData(subscriptionStatusResponse);
                }*/
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                NetworkUtils.onApiError(NewLoginActivity.this);
            }
        });
    }
}
