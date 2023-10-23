package com.abethu.app;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

import com.abethu.app.util.FontsOverride;
import com.facebook.FacebookSdk;
import com.trackier.sdk.TrackierSDK;
import com.trackier.sdk.TrackierSDKConfig;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AppController extends Application implements ComponentCallbacks2 {

    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //FontsOverride.setDefaultFont(this, "DEFAULT", "MyFontAsset.ttf");

        final String TR_SDK_KEY  = ""; // Please pass your SDK key here.

        /* While Initializing the SDK, You need to pass the three parameter in the TrackierSDKConfig.
         * In First argument, you need to pass context of the application
         * In second argument, you need to pass the Trackier SDK api key
         * In third argument, you need to pass the environment which can be either "development", "production" or "testing". */
        TrackierSDKConfig sdkConfig = new TrackierSDKConfig(this, TR_SDK_KEY,"development");
        TrackierSDK.initialize(sdkConfig);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/segoeui.ttf");
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/poppins_medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/

    }

    @Override
    protected void attachBaseContext(Context base) {
        //super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   "release your UI resources" is actually about things like caches.
                   You usually don't have to worry about managing views or UI components because the OS
                   already does that, and that's why there are all those callbacks for creating, starting,
                   pausing, stopping and destroying an activity.
                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.
                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.
                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }


}

