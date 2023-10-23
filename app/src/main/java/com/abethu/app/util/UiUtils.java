package com.abethu.app.util;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abethu.app.BuildConfig;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.abethu.app.R;

import okhttp3.logging.HttpLoggingInterceptor;


public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();
    private static Dialog loadingDialog;
    private static Dialog internetDialog;
    private static TranslateAnimation transform1,transform2,transform3,transform4;
    private static TranslateAnimation transform_1,transform_2,transform_3,transform_4;

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showNoInternetConnection(Context context)
    {
        hideNoInternetConnection();
        internetDialog = new Dialog(context);
        internetDialog.setContentView(R.layout.dialog_no_internet);
        WindowManager.LayoutParams params = internetDialog.getWindow().getAttributes(); // change this to your dialog.

        params.y = 10; // Here is the param to set your dialog position. Same with params.x
        internetDialog.getWindow().setAttributes(params);
        internetDialog.getWindow().setGravity(Gravity.BOTTOM);
        internetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        internetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        internetDialog.setCancelable(false);

        TextView btnOk = (TextView) internetDialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNoInternetConnection();
            }
        });

        try {
            if (!internetDialog.isShowing())
                internetDialog.show();
        }catch (Exception e)
        {}
    }

    public static void hideNoInternetConnection() {
        if (internetDialog != null && internetDialog.isShowing())
            try {
                internetDialog.dismiss();
            } catch (Exception e) {
                UiUtils.log(TAG, Log.getStackTraceString(e));
            }
    }

    public static void showLoadingDialog(Context context) {
        hideLoadingDialog();
        //loadingDialog = new SpotsDialog(context,"",R.style.SpotsDialogDefault,false,null);
        loadingDialog = new Dialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.api_loading_lottie);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //loadingDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView img = loadingDialog.findViewById(R.id.img);
        img.setBackgroundResource(R.drawable.anim_loader);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
            AnimationDrawable rocketAnimation = (AnimationDrawable) img.getBackground();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    rocketAnimation.start();
                }
            }).start();
            /*Ion.with(imgView)
                    .error(R.drawable.default_image)
                    .animateGif(AnimateGifMode.ANIMATE)
                    .load("file:///android_asset/animated.gif");*/
            //Glide.with(context).asGif().load("file:///android_asset/bebu_icon_gif.gif").into(img);
        }
    }

    public static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing())
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                UiUtils.log(TAG, Log.getStackTraceString(e));
            }
    }

    private static void setImageLoop(Context context, RelativeLayout root, ImageView imgInner) {
        // Need a thread to get the real size or the parent
        // container, after the UI is displayed
        imgInner.post(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation outAnim =
                        new TranslateAnimation(
                                -root.getWidth()+80f, root.getWidth(), 0f, 0f);
                // move from 0 (START) to width (PARENT_SIZE)
                outAnim.setInterpolator(new LinearInterpolator());
                outAnim.setRepeatMode(Animation.INFINITE); // repeat the animation
                outAnim.setRepeatCount(Animation.INFINITE);
                outAnim.setDuration(3000);

                AnimationSet set = new AnimationSet(true);

                Animation fadeIn = FadeIn(3000);
                fadeIn.setStartOffset(0);
                set.addAnimation(fadeIn);

                DisplayMetrics dm = new DisplayMetrics();
                //(Activity)context.getWindowManager().getDefaultDisplay().getMetrics(dm);
                int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

                int originalPos[] = new int[2];
                root.getLocationOnScreen(originalPos);

                int xDest = dm.widthPixels / 2;
                xDest -= (root.getMeasuredWidth() / 2);
                int yDest = dm.heightPixels / 2 - (root.getMeasuredHeight() / 2)
                        - statusBarOffset;

                TranslateAnimation anim = new TranslateAnimation(0, xDest
                        - originalPos[0], 0, yDest - originalPos[1]);
                anim.setDuration(3000);
                set.addAnimation(anim);

                /*Animation fadeWait = new AlphaAnimation(0.0f, 1.0f);
                try {
                    fadeWait.wait(1000);
                } catch (InterruptedException e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                set.addAnimation(fadeWait);*/

                Animation fadeOut = FadeOut(1000);
                fadeOut.setStartOffset(3000);
                set.addAnimation(fadeOut);

                set.setFillAfter(true);
                set.setFillEnabled(true);
                //splashImage.startAnimation(set);

                imgInner.startAnimation(set); // start first anim

            }
        });
    }

    private static Animation FadeIn(int t) {
        Animation fade;
        fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }

    private static Animation FadeOut(int t) {
        Animation fade;
        fade = new AlphaAnimation(1.0f, 0.1f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }

    public static ViewPropertyTransition.Animator animationObject = view -> {
//        view.setAlpha(0f);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(1000);
        fadeAnim.start();
    };

    public static boolean checkString(String value)
    {
        return value == null || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("");
    }

    public static void log(String tag, String message)
    {
        if(BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
