package com.abethu.app.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abethu.app.ui.fragment.bottomsheet.PaymentsInterface;
import com.abethu.app.util.download.Downloader;
import com.abethu.app.util.exoPlayer.SoundProgressChangeListner;
import com.abethu.app.util.exoPlayer.SoundView;
import com.abethu.app.util.sharedpref.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;

import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.cast.framework.CastContext;
import com.google.common.collect.ImmutableList;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.abethu.app.BuildConfig;
import com.abethu.app.R;
import com.abethu.app.model.Cast;
import com.abethu.app.model.DownloadUrl;
import com.abethu.app.model.GenreSeason;
import com.abethu.app.model.Invoice;
import com.abethu.app.model.Video;
import com.abethu.app.network.APIClient;
import com.abethu.app.network.APIConstants;
import com.abethu.app.network.APIInterface;
import com.abethu.app.ui.adapter.SeasonTitleAdapter;
import com.abethu.app.ui.adapter.VideoListAdapter;
import com.abethu.app.ui.adapter.VideoTileAdapter;
import com.abethu.app.ui.adapter.VideoWatchNextAdapter;
import com.abethu.app.util.NetworkUtils;
import com.abethu.app.util.ParserUtils;
import com.abethu.app.util.ResponsivenessUtils;
import com.abethu.app.util.exoPlayer.TrackSelectionDialog;
import com.abethu.app.util.UiUtils;
import com.abethu.app.util.download.DownloadCompleteListener;
import com.abethu.app.util.download.DownloadUtils;
import com.abethu.app.util.sharedpref.PrefKeys;
import com.abethu.app.util.sharedpref.PrefUtils;
import com.skydoves.elasticviews.ElasticAnimation;
import com.skydoves.elasticviews.ElasticFinishListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abethu.app.network.APIConstants.Constants.DOWNLOAD_STATUS;
import static com.abethu.app.network.APIConstants.Params.VERSION_CODE;
import static com.abethu.app.ui.activity.MainActivity.connectivityReceiver;
import static com.abethu.app.ui.activity.MainActivity.dimension;
import static com.abethu.app.util.UiUtils.checkString;
import static com.abethu.app.util.sharedpref.Utils.getTime;
import static com.google.android.exoplayer2.C.TRACK_TYPE_VIDEO;
import static com.google.android.exoplayer2.util.Util.getUserAgent;
import static com.abethu.app.network.APIConstants.APIs.DOWNLOAD_URLS;
import static com.abethu.app.network.APIConstants.APIs.SUGGESTION_VIDEOS;
import static com.abethu.app.network.APIConstants.Constants;
import static com.abethu.app.network.APIConstants.Constants.CANCELLED_OR_COMPLETED;
import static com.abethu.app.network.APIConstants.Constants.DOWNLOAD_UPDATE;
import static com.abethu.app.network.APIConstants.Constants.VIDEO_ELAPSED;
import static com.abethu.app.network.APIConstants.Params;
import static com.abethu.app.network.APIConstants.Params.ADMIN_VIDEO_ID;
import static com.abethu.app.network.APIConstants.Params.COOKIE_KEY_PAIR;
import static com.abethu.app.network.APIConstants.Params.COOKIE_POLICY;
import static com.abethu.app.network.APIConstants.Params.COOKIE_SIGNATURE;
import static com.abethu.app.network.APIConstants.Params.DATA;
import static com.abethu.app.network.APIConstants.Params.DOWNLOAD_RESOLUTION;
import static com.abethu.app.network.APIConstants.Params.ERROR_MESSAGE;
import static com.abethu.app.network.APIConstants.Params.ID;
import static com.abethu.app.network.APIConstants.Params.MAIN;
import static com.abethu.app.network.APIConstants.Params.MESSAGE;
import static com.abethu.app.network.APIConstants.Params.ORIGINAL;
import static com.abethu.app.network.APIConstants.Params.PARENT_ID;
import static com.abethu.app.network.APIConstants.Params.RESPONSE;
import static com.abethu.app.network.APIConstants.Params.SEASON_ID;
import static com.abethu.app.network.APIConstants.Params.SUBTITLE_URL;
import static com.abethu.app.network.APIConstants.Params.SUCCESS;
import static com.abethu.app.network.APIConstants.Params.TRAILER;
import static com.abethu.app.network.APIConstants.Params.VIDEO;
import static com.abethu.app.network.APIConstants.Params.VIDEO_URL;
import static com.abethu.app.ui.activity.MainActivity.COOKIEKEYPAIR;
import static com.abethu.app.ui.activity.MainActivity.COOKIEPOLICY;
import static com.abethu.app.ui.activity.MainActivity.COOKIESIGNATURE;
import static com.abethu.app.ui.activity.MainActivity.CURRENT_IP;
import static com.abethu.app.ui.activity.MainActivity.mFirebaseAnalytics;
import static com.abethu.app.util.ParserUtils.getFileExtension;
import static com.abethu.app.util.UiUtils.animationObject;
import static com.abethu.app.util.download.DownloadUtils.isValidVideoFile;
import static com.abethu.app.util.sharedpref.PrefKeys.VIDEO_AGE_VERIFICATION;
import static com.abethu.app.util.sharedpref.Utils.getUserLoginStatus;

public class VideoPageActivity extends BaseActivity implements DownloadCompleteListener,
        PaymentsInterface,
        VideoListAdapter.OnDataChangedListener, SessionAvailabilityListener, DialogInterface.OnClickListener,
        NumberPicker.OnValueChangeListener, StyledPlayerControlView.VisibilityListener{

    private static final String TAG = VideoPageActivity.class.getSimpleName();

    public static final String ACTION_DOWNLOAD_UPDATE = BuildConfig.APPLICATION_ID + DOWNLOAD_UPDATE;
    int downloadID = 0;

    @BindView(R.id.playVideoBtn)
    ImageView playVideoBtn;
    @BindView(R.id.moreLikeThisRecycler)
    RecyclerView moreLikeThisRecycler;
    @BindView(R.id.rvSeasonPicker)
    RecyclerView rvSeasonPicker;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.seriesLayout)
    ViewGroup seriesLayout;
    @BindView(R.id.trailersLayout)
    ViewGroup trailersLayout;
    @BindView(R.id.trailerRecycler)
    RecyclerView trailerRecycler;
    @BindView(R.id.videoThumbnail)
    ImageView videoThumbnail;
    @BindView(R.id.rl_video_title)
    RelativeLayout rl_video_title;
    @BindView(R.id.btnTrailer)
    TextView btnTrailer;
    @BindView(R.id.img_rating)
    ImageView img_rating;
    @BindView(R.id.videoTitle)
    TextView videoTitle;
    @BindView(R.id.descHeader)
    TextView descHeader;
    @BindView(R.id.infoHeader)
    TextView infoHeader;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.directorText)
    TextView directorText;
    @BindView(R.id.starringText)
    TextView starringText;
    @BindView(R.id.genreText)
    TextView genreText;
    @BindView(R.id.ratingText)
    TextView ratingText;
    @BindView(R.id.video_rating)
    ImageView video_rating;
    @BindView(R.id.tvWatchNext)
    TextView tvWatchNext;
    @BindView(R.id.videoContainer)
    RelativeLayout videoContainer;
    @BindView(R.id.nestedScrollVideoPage)
    NestedScrollView nestedScrollVideoPage;
    @BindView(R.id.downloadBtn)
    ImageView downloadView;
    @BindView(R.id.ll_downloadingView)
    LinearLayout ll_downloadingView;
    @BindView(R.id.downloadingView)
    LottieAnimationView downloadingView;
    @BindView(R.id.pauseDownloadingView)
    ImageView pauseDownloadingView;
    @BindView(R.id.cancelDownloadingView)
    ImageView cancelDownloadingView;
    @BindView(R.id.downloadingView_percent)
    public TextView downloadingView_percent;
    @BindView(R.id.ll_downloadedView)
    LinearLayoutCompat ll_downloadedView;
    @BindView(R.id.playDownloadedView)
    ImageView playDownloadedView;
    @BindView(R.id.deleteDownloadedView)
    ImageView deleteDownloadedView;
    @BindView(R.id.noVideosForThisSeason)
    TextView noVideosForThisSeason;
    @BindView(R.id.exoplayer)
    StyledPlayerView mExoPlayerView;
    @BindView(R.id.exo_player_progress)
    SpinKitView progressBar;
    @BindView(R.id.media_route_button)
    MediaRouteButton mediaRouteButton;
    @BindView(R.id.rlThumbnail)
    RelativeLayout rlThumbnail;
    @BindView(R.id.rlPlayer)
    RelativeLayout rlPlayer;
    @BindView(R.id.full_Screen_Right_Top)
    ImageView full_Screen_Right_Top;

    //Player
    @BindView(R.id.exo_unlock)
    ImageView exoUnLock;
    @BindView(R.id.volumeview)
    SoundView soundView;
    @BindView(R.id.volumecontainer)
    ConstraintLayout volumecontainerView;
    @BindView(R.id.progress_volume_text)
    TextView progress_volume_text;
    @BindView(R.id.volumeicon)
    ImageView volumeicon;

    @BindView(R.id.brightnessview)
    SoundView brightnessView;
    @BindView(R.id.brightnesscontainer)
    ConstraintLayout brightnesscontainerView;
    @BindView(R.id.progress_brightness_text)
    TextView progress_brightness_text;
    @BindView(R.id.brightnessicon)
    ImageView brightnessicon;

    @BindView(R.id.banner_video_title)
    TextView banner_video_title;
    @BindView(R.id.genreTextBanner)
    TextView genreTextBanner;
    @BindView(R.id.img_thumbnail)
    ImageView img_thumbnail;

    @BindView(R.id.ll_exo_rating)
    LinearLayout ll_exo_rating;
    @BindView(R.id.exo_txt_rating)
    TextView exo_txt_rating;
    @BindView(R.id.exo_txt_genre)
    TextView exo_txt_genre;

    @BindView(R.id.rl_exo_link)
    RelativeLayout rl_exo_link;

    @BindView(R.id.drop_down12)
    ImageView drop_down12;
    @BindView(R.id.spin_child_name)
    Spinner codeSpinner;

    int descLength = 100;

    private Activity activity;
    private Video video;
    private APIInterface apiInterface;
    private PrefUtils prefUtils;
    private VideoWatchNextAdapter trailersAdapter;
    private VideoListAdapter seasonsAdapter;
    private SeasonTitleAdapter seasonTitleAdapter;
    private Invoice invoice;
    private String currentlyStreamingUrl;
    public static boolean trailerPlaying = false;
    public static boolean videoPlaying = false;
    private ArrayList<PlayerMessage> playerMessages = new ArrayList<>();
    private ArrayList<String> timers = new ArrayList<>();
    //private HlsMediaSource mVideoSource;
    private MediaSource mVideoSource;
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private List<Subtitle> subtitles = new ArrayList<>();
    private ImageView mFullScreenIcon;
    private ImageView exoBackBtn;

    //    private TrackSelectionHelper trackSelectionHelper;
//    private SubtitlesSelectionHelper subtitlesSelectionHelper;
    private DefaultTrackSelector trackSelector;
    private ExoPlayer player;
    private StyledPlayerControlView controlView;
    private int playbackPosition;
    private boolean mExoPlayerFullscreen = false;
    private boolean mExoPlayerIsLocked = false;
    private MergingMediaSource mergedSource;
    private Dialog mFullScreenDialog;
    private CastContext castContext;
    private CastPlayer castPlayer;

    private boolean isscalegesture;
    private float downy;
    private float endheight;
    private float diffheight;
    private int currentprogress;
    private int currentbrightprogress;
    private float lastx;
    private boolean first = true, second = true;
    private int selected = 0;
    StyledPlayerView exoplayer;
    float scaleFactor = 1.0f;
    float minScaleFactor = 1.0f;
    float maxScaleFactor = 3.0f;

    public static final int PAY_PAL_REQUEST_CODE = 200;
    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(APIConstants.Payments.PayPal.CLIENT_ID);

    RelativeLayout btnSubtitles;
    RelativeLayout mFullScreenButton;
    ImageButton exo_play,exo_pause;
    ImageButton exoLock;

    /**
     * DS for showing season based/more like this videos
     */
    ArrayList<GenreSeason> totalSeasons = new ArrayList<>();
    private ArrayList<Video> relatedVideos = new ArrayList<>();
    private ArrayList<Video> seasonVideos = new ArrayList<>();
    private View.OnClickListener needToSubscribeToDownloadListener = v -> new AlertDialog.Builder(this)
            .setTitle(R.string.need_subcripton)
            .setMessage(R.string.you_need_subcribe_to_a_plan)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                Intent needPaymentIntent = new Intent(VideoPageActivity.this, PlansActivity.class);
                startActivity(needPaymentIntent);
            })
            .setNegativeButton(getString(R.string.no), null)
            .create().show();
    private View.OnClickListener downloadVideoListener = v -> {
        ArrayList<DownloadUrl> resolutions = video.getDownloadResolutions();
        if (resolutions!=null && resolutions.size() == 0)
            UiUtils.showShortToast(this, getString(R.string.no_suitable_download_urls));
        else if (resolutions!=null && resolutions.size() == 1) {
            if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
                PRDownloader.resume(downloadID);
                return;
            }
            if(NetworkUtils.isNetworkConnected(VideoPageActivity.this))
                downloadVideo(resolutions.get(0).getUrl());
            else
                UiUtils.showShortToast(VideoPageActivity.this, getString(R.string.check_internet));
        }
        else {
            if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
                PRDownloader.resume(downloadID);
                return;
            }
            if(NetworkUtils.isNetworkConnected(VideoPageActivity.this))
                showDownloadResolutionDialog(resolutions);
            else
                UiUtils.showShortToast(VideoPageActivity.this, getString(R.string.check_internet));
            //showDownloadResolutionDialogChooser(resolutions);
        }
        //showDownloadResolutionDialogChooser(resolutions);
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int videoID;

                if (video.getIsMain() == 1 && video.getIsSingle() == 0)
                    videoID = seasonVideos.get(0).getAdminVideoId();
                else
                    videoID = video.getAdminVideoId();
                if (intent != null && intent.getIntExtra(ADMIN_VIDEO_ID, 0) == videoID) {
                    //true = completed, false= cancelled
                    boolean isCancelledOrCompleted = intent.getBooleanExtra(CANCELLED_OR_COMPLETED, false);
                    int downloadStatus = intent.getIntExtra(DOWNLOAD_STATUS, 1);
                    switch(downloadStatus)
                    {
                        case 1:
                        case 2:
                            showDownloadingButton();
                            break;
                        case 3:
                        case 5:
                            showDownloadButton(downloadVideoListener);
                            break;
                        case 4:
                            showPlayButton();
                            break;
                        /*case 5:
                            showDownloadButton(downloadVideoListener);
                            break;*/
                    }
                    /*if (isCancelledOrCompleted)
                        showPlayButton();
                    else
                        showDownloadButton(downloadVideoListener);*/
                }
            } catch (Exception e) {
            }
        }
    };
    private View.OnClickListener needToPayToDownloadListener = v -> new AlertDialog.Builder(this)
            .setTitle(R.string.need_payment)
            .setMessage(R.string.you_need_to_pay_for_this_video)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            })
//            .setNegativeButton(getString(R.string.no), null)
            .create().show();
    private boolean isShowingTrackSelectionDialog;

    Timer mRestoreOrientation;
    int parentId=-1, seasonID =-1;
    boolean isAdultAccepted = false;

    Handler mVolHandler=new Handler();
    Runnable mVolRunnable=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Animation fadeOut = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.fade_out);
            volumecontainerView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    volumecontainerView.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        }
    };

    Handler mBrightHandler=new Handler();
    Runnable mBrightRunnable=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Animation fadeOut = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.fade_out);
            brightnesscontainerView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    brightnesscontainerView.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        }
    };

    Handler mLockHandler=new Handler();
    Runnable mLockRunnable=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Animation fadeOut = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.fade_out);
            exoUnLock.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    exoUnLock.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        }
    };

    Handler mRatingHandler=new Handler();
    Runnable mRatingRunnable=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Animation slide_down_fade_out  = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.slide_down_fade_out);
            ll_exo_rating.startAnimation(slide_down_fade_out);

            slide_down_fade_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    ll_exo_rating.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        }
    };
    boolean zoom=false;
    AudioManager audioManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        ButterKnife.bind(this);
        UiUtils.log(TAG,"VideoPageActivity");
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        activity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        /*Glide.with(this).load(getIntent().getStringExtra(IMAGE)).into(videoThumbnail);
        videoTitle.setText(getIntent().getStringExtra(TITLE));
        video = (Video) getIntent().getSerializableExtra(VIDEO);
        videoThumbnail.setTransitionName(getIntent().getStringExtra(TRANSITION_IMAGE));
        videoTitle.setTransitionName(getIntent().getStringExtra(TRANSITION_TITLE));
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_exit_transation));*/
        //showVideoData();

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        /*id = prefUtils.getIntValue(PrefKeys.USER_ID, 1);
        subProfileId = prefUtils.getIntValue(PrefKeys.ACTIVE_SUB_PROFILE, 0);*/
        initFullscreenButton();
        initFullscreenDialog();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        rlThumbnail.setLayoutParams(ResponsivenessUtils.getLayoutParamsForFullWidthSeasonView(this));
        rlPlayer.setLayoutParams(ResponsivenessUtils.getLayoutParamsForFullWidthSeasonView(this));
        img_thumbnail.setLayoutParams(ResponsivenessUtils.getLayoutParamsForHorizontalAdapter(this));
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();

//        String data = appLinkData.getQueryParameter("");

        full_Screen_Right_Top=findViewById(R.id.full_Screen_Right_Top);
        exoplayer = findViewById(R.id.exoplayer);
        full_Screen_Right_Top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoom==false){
                    full_Screen_Right_Top.setBackgroundResource(R.drawable.ic_baseline_zoom_in_map_24);
                    exoplayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    zoom=true;
                }else{
                    full_Screen_Right_Top.setBackgroundResource(R.drawable.ic_baseline_zoom_out_map_24);
                    exoplayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    Log.d("onClick_1", "onClick: 2");
                    zoom=false;
                }
                //          Toast.makeText(activity, "bala wo bala", Toast.LENGTH_SHORT).show();
            }
        });



        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description.getText().toString().contains("more"))
                {
                    //popup
                    showFullDescription();
                }
            }
        });

        codeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = String.valueOf(codeSpinner.getSelectedItem());
                /*if (position == 0) {
                    codeSpinner.setSelection(0);
                }*/
                UiUtils.log(TAG,"Spin--> "+ code);
                UiUtils.log(TAG,"Position--> "+ position);
                updateSeason(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drop_down12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeSpinner.performClick();

            }
        });

        checkWidth();
    }

    private void handleIntent(Intent intent) {
//        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
//            String recipeId = appLinkData.getLastPathSegment();
//            Uri appData = Uri.parse("content://com.recipe_app/recipe/").buildUpon()
//                    .appendPath(recipeId).build();
//            loadVideo();
//        }
    }
    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, maxScaleFactor));
            exoplayer.setScaleX(scaleFactor);
            exoplayer.setScaleY(scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // Optional: You can perform any action after the scaling ends.
        }
    }
    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VideoPageActivity.ACTION_DOWNLOAD_UPDATE);
        registerReceiver(receiver, filter);
        IntentFilter filterInternet = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filterInternet);
        super.onResume();
        id = prefUtils.getIntValue(PrefKeys.USER_ID, 1);
        token = prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "");
        subProfileId = prefUtils.getIntValue(PrefKeys.ACTIVE_SUB_PROFILE, 0);
        loadVideo();
    }

    private void loadVideo() {
        Intent intent = getIntent();
        if (intent != null) {
            int adminVideoId = intent.getIntExtra(ADMIN_VIDEO_ID, -1);
            parentId = intent.getIntExtra(PARENT_ID, -1);
            if(parentId==-1 || parentId==0)
                parentId = adminVideoId;
            seasonID = intent.getIntExtra(SEASON_ID, -1);
            if(PrefUtils.getInstance(activity).getBoolanValue(PrefKeys.IS_LOGGED_IN, false))
                getCookieData(adminVideoId);
            else
                loadVideoData(adminVideoId);
        } else {
            UiUtils.showShortToast(this, getString(R.string.please_reopen));
            finish();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        //if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !isFinishing()){
        new Instrumentation().callActivityOnSaveInstanceState(this, new Bundle());
        //}
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    /**
     * Load video data from backend based on admin video subProfileIdUnderChange
     * Call loadGenreAndTrailers after you get successful response
     */
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getCookieData(int adminVideoId) {
        UiUtils.showLoadingDialog(activity);
        APIInterface apiInterfaceNew = APIClient.getClient().create(APIInterface.class);
        Call<String> call = apiInterfaceNew.getAllCookies(APIConstants.APIs.GET_ALL_COOKIES);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject cookieResponse = null;
                try {
                    cookieResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (cookieResponse != null) {
                    if (cookieResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONObject data = cookieResponse.optJSONObject(DATA);
                        COOKIEKEYPAIR = data.optString(COOKIE_KEY_PAIR);
                        COOKIEPOLICY = data.optString(COOKIE_POLICY);
                        COOKIESIGNATURE = data.optString(COOKIE_SIGNATURE);

                        Log.d("testebfibqewf", "onResponse: 1  "+data.optString(COOKIE_KEY_PAIR));
                        Log.d("testebfibqewf", "onResponse: 1  "+data.optString(COOKIE_POLICY));
                        Log.d("testebfibqewf", "onResponse: 1  "+data.optString(COOKIE_SIGNATURE));
                        UiUtils.log(TAG,"COOKIEKEYPAIR:"+COOKIEKEYPAIR
                                +"\nCOOKIEPOLICY:"+COOKIEPOLICY
                                +"\nCOOKIESIGNATURE:"+COOKIESIGNATURE);
                        loadVideoData(adminVideoId);
                    } else {
                        UiUtils.showShortToast(activity, cookieResponse.optString(ERROR_MESSAGE));
                    }
                }else
                {
                    UiUtils.log(TAG, "Cookies api error");
                    loadVideoData(adminVideoId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(activity);
                loadVideoData(adminVideoId);
            }
        });
    }
///////////////////////////////////////////////API////////////////////////////////////
    private void loadVideoData(int adminVideoId) {
        UiUtils.showLoadingDialog(this);
        nestedScrollVideoPage.setVisibility(View.GONE);
        Map<String, String> headers = new HashMap<>();
        headers.put(Params.COOKIE_KEY_PAIR, COOKIEKEYPAIR);
        headers.put(Params.COOKIE_POLICY, COOKIEPOLICY);
        headers.put(Params.COOKIE_SIGNATURE, COOKIESIGNATURE);

        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, id);
        params.put(Params.TOKEN, token);
        params.put(Params.SUB_PROFILE_ID, subProfileId);
        params.put(ADMIN_VIDEO_ID, adminVideoId);
        params.put(Params.PARENT_ID, parentId);
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.getVideoData(APIConstants.APIs.GET_SINGLE_VIDEO_DATA, headers, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.d("onResponse_testghd", "onResponse: "+response.body());
                UiUtils.hideLoadingDialog();
                nestedScrollVideoPage.setVisibility(View.VISIBLE);
                JSONObject videoResponse = null;
                try {
                    videoResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (videoResponse != null) {
                    if (videoResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONObject data = videoResponse.optJSONObject(DATA);
                        video = ParserUtils.parseVideoData(data);
                        showVideoData();
                        getPlayerJsonFile(adminVideoId/*data.optString(APIConstants.Params.PLAYER_JSON)*/);
                        if(video.getA_record()!=1)
                            isAdultAccepted=true;
                        //getVideo related data
                        btnTrailer.setVisibility(View.VISIBLE);
                        if(video.getIsSingle()!=0)
                        {
                            UiUtils.log(TAG, video.getIsSingle()==1?"Song Selected":"Short Video Selected");
                            rvSeasonPicker.setVisibility(View.GONE);
                            if(checkString(video.getTrailerVideo())) {
                                btnTrailer.setVisibility(View.GONE);
                                //btnTrailer.setWidth(1);
                                //btnTrailer.setHeight(1);
                            }
                            parseAndShowGenres(adminVideoId);
                        }
                        else
                            loadGenreAndTrailers(adminVideoId);
                        //loadGenreAndTrailers(adminVideoId);
                    } else {
                        UiUtils.showShortToast(VideoPageActivity.this, videoResponse.optString(ERROR_MESSAGE));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                nestedScrollVideoPage.setVisibility(View.VISIBLE);
                finish();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }

    private void showWarning(Video video) {

        UiUtils.log(TAG,"Video Rating: "+ video.getRatings());
        if(video.getRatings()!=0 && video.getRatings()>2) {
            //Now we need an AlertDialog.Builder object
            Dialog dialog = new Dialog(activity);

            dialog.setContentView(R.layout.dialog_terms_and_privacy);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);

            dialog.findViewById(R.id.submit_btn).setOnClickListener((View v) -> {
                dialog.dismiss();

                String json = prefUtils.getStringValue(VIDEO_AGE_VERIFICATION,"{}");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    jsonObject.put(""+(id)+(video.getAdminVideoId()),true);
                } catch (JSONException e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                prefUtils.setValue(VIDEO_AGE_VERIFICATION,jsonObject.toString());
            });


            TextView termsCondition = dialog.findViewById(R.id.termsCondition);
            termsCondition.setVisibility(View.GONE);
            TextView termsPrivacy = dialog.findViewById(R.id.termsPrivacy);
            termsPrivacy.setVisibility(View.GONE);
            TextView videoRatings = dialog.findViewById(R.id.video_ratings);
            videoRatings.setVisibility(View.VISIBLE);

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == KeyEvent.ACTION_UP) {
                        // TODO do the "back pressed" work here
                        finish();
                        return true;
                    }
                    return false;
                }
            });

            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    ///////////////////////////////////////////////API////////////////////////////////////
    private void getPlayerJsonFile(int jsonFile) {
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, id);
        params.put(ADMIN_VIDEO_ID, jsonFile);

        Call<String> call = apiInterface.getPlayerJson(DOWNLOAD_URLS, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject videoResponse = null;
                try {
                    videoResponse = new JSONObject(response.body());

                    if (videoResponse != null) {
                        JSONArray downloadArray = videoResponse.optJSONArray(DOWNLOAD_RESOLUTION);
                        video.setDownloadResolutions(parseDownloadResolutions(downloadArray));
                    }
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                finish();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }

    private static ArrayList<DownloadUrl> parseDownloadResolutions(JSONArray downloadResolutionsArr) {
        ArrayList<DownloadUrl> downloadUrls = new ArrayList<>();
        if (downloadResolutionsArr == null)
            return downloadUrls;

        for (int i = 0; i < downloadResolutionsArr.length(); i++) {
            try {
                JSONObject downloadResObj = downloadResolutionsArr.getJSONObject(i);
                downloadUrls.add(new DownloadUrl(downloadResObj.optString(Params.TITLE),
                        downloadResObj.optString(VIDEO),
                        downloadResObj.optString(Params.RESOLUTION),
                        UiUtils.checkString(downloadResObj.optString(Params.LANGUAGE))? "":downloadResObj.optString(Params.LANGUAGE)));
            } catch (JSONException e) {
                UiUtils.log(TAG,Log.getStackTraceString(e));
            }
        }
        return downloadUrls;
    }

    /**
     * Loads trailers and More LIke this video / Season based videos with a spinner
     * Set a spinner after getting response based on whether video is Season based
     */


    ///////////////////////////////////////////////API////////////////////////////////////
    private void loadGenreAndTrailers(int adminVideoId) {

        Map<String, String> headers = new HashMap<>();
        headers.put(Params.COOKIE_KEY_PAIR, COOKIEKEYPAIR);
        headers.put(Params.COOKIE_POLICY, COOKIEPOLICY);
        headers.put(Params.COOKIE_SIGNATURE, COOKIESIGNATURE);

        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, id);
        params.put(Params.TOKEN, token);
        params.put(Params.SUB_PROFILE_ID, subProfileId);
        params.put(ADMIN_VIDEO_ID, adminVideoId);
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.getVideoRelatedData(APIConstants.APIs.GET_SINGLE_VIDEO_RELATED_DATA, headers, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject genreTrailerResponse = null;
                try {
                    genreTrailerResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                }
                if (genreTrailerResponse != null) {
                    if (genreTrailerResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONObject data = genreTrailerResponse.optJSONObject(DATA);
                        ParserUtils.parseVideoRelatedData(video, data);
                        parseAndShowGenres(adminVideoId);

                        UiUtils.log(TAG,"Trailer Url: "+ video.getTrailerVideos().toString());
                        UiUtils.log(TAG,"Video Url: "+ video.getVideoUrl());
                        Log.d("onVideoPlayClickedty", "onResponse video: "+video.getVideoUrl().toString());
                        Log.d("onVideoPlayClickedty", "TrailerVideos: "+video.getTrailerVideos().toString());
                        btnTrailer.setVisibility(View.VISIBLE);
                        //rvSeasonPicker.setVisibility(View.VISIBLE);

                    } else {
                        UiUtils.showShortToast(VideoPageActivity.this, genreTrailerResponse.optString(ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }

    /**
     * Set moreLikethis adapater and show More like this/ Season based videos based on click of the spinner
     * @param adminVideoId
     */
    private void parseAndShowGenres(int adminVideoId) {
        if (video.isSeasonVideo()) {
            seasonsAdapter = new VideoListAdapter(this, video.getAdminVideoId(), seasonVideos, VideoListAdapter.VideoListType.TYPE_SEASONS, this);
            moreLikeThisRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            moreLikeThisRecycler.setHasFixedSize(true);
            moreLikeThisRecycler.setAdapter(seasonsAdapter);
        }

        if(video.getIsSingle()==0) {
            boolean spinnerRequired = video.isSeasonVideo() && video.getGenreSeasons().size() > 0;
            //rvSeasonPicker.setVisibility(spinnerRequired ? View.VISIBLE : View.GONE);
            //Get videos
            if (video.isSeasonVideo()) {
                if (spinnerRequired) {
                    int position = 0;
                    //Get the seasons as a string list, Set this data to Spinner through adapter,
                    ArrayList<GenreSeason> genreSeasons = video.getGenreSeasons();
                    for (int i = 0; i < genreSeasons.size(); i++) {
                        if (video.getSeasonId() == genreSeasons.get(i).getId()) {
                            genreSeasons.get(i).setSelected(true);
                            position = i;
                        }
                    }
                    seasonTitleAdapter = new SeasonTitleAdapter(this, genreSeasons);
                    rvSeasonPicker.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    rvSeasonPicker.setAdapter(seasonTitleAdapter);
                    rvSeasonPicker.scrollToPosition(position);
                    seriesLayout.setVisibility(View.VISIBLE);
                }
                getVideosForGenre(video.getSeasonId(), 0);
            }
        }

        trailersAdapter = new VideoWatchNextAdapter(this, relatedVideos, VideoTileAdapter.VIDEO_SECTION_TYPE_NORMAL_HORIZONTAL, true);
        trailerRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        trailerRecycler.setHasFixedSize(true);
        trailerRecycler.setAdapter(trailersAdapter);
        getSuggestionVideosWith(0,adminVideoId);


        if(video.getIsSingle()==0) {
            totalSeasons = video.getGenreSeasons();
            //totalSeasons.add(video.getGenreSeasons().get(0));
            UiUtils.log(TAG, "totalSeasons:" + totalSeasons.toString());
            ArrayAdapter<GenreSeason> dataAdapter = new ArrayAdapter<GenreSeason>(this, R.layout.custom_spinner_layout, totalSeasons);
            codeSpinner.setAdapter(dataAdapter);

            for (int i = 0; i < totalSeasons.size(); i++) {
                UiUtils.log(TAG, "totalSeasons:" + i + " -->" + totalSeasons.get(i).isSelected());
                if (totalSeasons.get(i).isSelected()) {
                    codeSpinner.setSelection(i);
                }
            }
        }

    }

    public void updateSeason(int position) {
        video.setSeasonId(video.getGenreSeasons().get(position).getId());
        video.setSeasonName(video.getGenreSeasons().get(position).getName());
        ArrayList<GenreSeason> genreSeasons = video.getGenreSeasons();
        for (int i = 0; i < genreSeasons.size(); i++) {
            if (video.getSeasonId() == genreSeasons.get(i).getId()){
                genreSeasons.get(i).setSelected(true);
            }else {
                genreSeasons.get(i).setSelected(false);
            }
        }
        seasonTitleAdapter.notifyDataSetChanged();
        getVideosForGenre(video.getSeasonId(), 0);
    }
    ///////////////////////////////////////////////API////////////////////////////////////
    //Get suggestion videos
    private void getSuggestionVideosWith( int skip,int adminVideoID) {
        Map<String, Object> params = new HashMap<>();
        params.put(APIConstants.Params.ID, id);
        params.put(APIConstants.Params.TOKEN, token);
        params.put(APIConstants.Params.SUB_PROFILE_ID, subProfileId);
        params.put(APIConstants.Params.SKIP, skip);
        params.put(APIConstants.Params.CATEGORY_ID, 0);
        params.put(APIConstants.Params.SUB_CATEGORY_ID, 0);
        params.put(APIConstants.Params.GENRE_ID, 0);
        params.put(APIConstants.Params.PAGE_TYPE, "");
        params.put(ADMIN_VIDEO_ID, adminVideoID);
        params.put(APIConstants.Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));
        params.put(APIConstants.Params.IP, CURRENT_IP);
        params.put(APIConstants.Params.APPVERSION, BuildConfig.VERSION_NAME);
        params.put(VERSION_CODE, BuildConfig.VERSION_CODE);

        Call<String> call = apiInterface.getSuggestionVideos(SUGGESTION_VIDEOS, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject suggestionVideosResponse = null;
                try {
                    suggestionVideosResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (suggestionVideosResponse != null) {
                    ArrayList<Video> tempSuggestions =  new ArrayList<>();//seasonVideos;
                    if (suggestionVideosResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONArray data = suggestionVideosResponse.optJSONArray(DATA);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject videoObj;
                            try {
                                videoObj = data.getJSONObject(i);
                                Video video = ParserUtils.parseVideoData(videoObj);
                                tempSuggestions.add(video);
                            } catch (JSONException e) {
                                UiUtils.log(TAG, Log.getStackTraceString(e));
                            }
                        }

                        if (skip == 0) {
                            relatedVideos.clear();
                            relatedVideos.addAll(tempSuggestions);
                        } else
                            relatedVideos.addAll(tempSuggestions);

                        onMoreLikeThisDataChanged();
                    } else {
                        UiUtils.showShortToast(VideoPageActivity.this, suggestionVideosResponse.optString(ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    /**
     * COde to change view when data chnages in first tab (MOre like this)
     */
    private void onMoreLikeThisDataChanged() {
        boolean isEmpty = relatedVideos.size() == 0;
        trailersLayout.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        trailersAdapter.notifyDataSetChanged();
    }

    private void seasonDataChanged() {
        boolean isEmpty = seasonVideos.size() == 0;
        noVideosForThisSeason.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        moreLikeThisRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        seasonsAdapter.notifyDataSetChanged();
    }


    /**
     * Backend API, Get the videos
     */

    ///////////////////////////////////////////////API////////////////////////////////////
    private void getVideosForGenre(int seasonId, int skip) {
        video.setSeasonId(seasonId);

        ArrayList<Video> videosForSeason = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, id);
        params.put(Params.TOKEN, token);
        params.put(Params.PARENT_ID, parentId);
        params.put(Params.SUB_PROFILE_ID, subProfileId);
        params.put(Params.PAGE_TYPE,"SERIES");
        params.put(Params.CATEGORY_ID,  video.getCategoryId());
        params.put(Params.SUB_CATEGORY_ID, video.getSubCategoryId());
        params.put(Params.GENRE_ID, seasonId);
        params.put(SEASON_ID, seasonId);
        params.put(Params.ADMIN_VIDEO_ID, video.getAdminVideoId());
        params.put(Params.SKIP, skip);
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.getVideosForSeason(APIConstants.APIs.GET_VIDEOS_FOR_SEASON_NEW, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject seasonVideoResponse = null;
                try {
                    seasonVideoResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (seasonVideoResponse != null) {
                    if (seasonVideoResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONArray data = seasonVideoResponse.optJSONArray(DATA);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject videoObj;
                            try {
                                videoObj = data.getJSONObject(i);
                                Video video = ParserUtils.parseVideoData(videoObj);
                                videosForSeason.add(video);
                            } catch (JSONException e) {
                                UiUtils.log(TAG, Log.getStackTraceString(e));
                            }
                        }

                        if (skip == 0)
                            seasonVideos.clear();

                        seasonVideos.addAll(videosForSeason);
                        seasonDataChanged();
                    } else {
                        UiUtils.log(TAG,seasonVideoResponse.optString(ERROR_MESSAGE));
                        //UiUtils.showShortToast(VideoPageActivity.this, seasonVideoResponse.optString(ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }

    /**
     * Showing video data in the UI
     */
    private void showVideoData() {
        Glide.with(getApplicationContext())
                .load(video.getThumbNailUrl())
                .transition(GenericTransitionOptions.with(animationObject))
                .apply(new RequestOptions().placeholder(R.drawable.abethu_placeholder_horizontal).error(R.drawable.abethu_placeholder_horizontal).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                .thumbnail(0.5f)
                .into(videoThumbnail);

        //setRating(img_rating,video.getA_record());
        //setRating(null,ratingText,video.getA_record());
        setRating(null,exo_txt_rating,video.getA_record());

        //UiUtils.log(TAG,"Vertical Image -> Trailer -> " + video.getTrailerBrowseImage());
        Glide.with(getApplicationContext())
                .load(video.getBrowseImage())
                .transition(GenericTransitionOptions.with(animationObject))
                .apply(new RequestOptions().placeholder(R.drawable.abethu_placeholder_vertical).error(R.drawable.abethu_placeholder_vertical).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(0)))
                .thumbnail(0.5f)
                .into(img_thumbnail);

        if(video.getIsMain()==1) {
            videoTitle.setText(video.getTitle());
            banner_video_title.setText(video.getTitle());
        }
        else {
            videoTitle.setText(video.getEpTitle());
            banner_video_title.setText(video.getEpTitle());
        }
//        if (!video.getUserType().equalsIgnoreCase("0")) {
//            Log.d("onVideoPlayClicked", "onVideoPlayClicked: 001");
//        }else{
//            Log.d("onVideoPlayClicked", "onVideoPlayClicked: 002");
//            downloadView.setVisibility(View.GONE);
//        }
        videoTitle.setSelected(true);
        banner_video_title.setSelected(true);

        /*int width=img_thumbnail.getWidth();
        int height=img_thumbnail.getHeight();
        UiUtils.log(TAG, "Height: " + height + " Width: " + width);

        if(height<550) {
            description.setMaxLines(4);
        }
        else if(height>550 && height<700) {
            if(width>450) {
                descLength = 200;
                description.setMaxLines(6);
            }else
            {
                descLength = 400;
                description.setMaxLines(6);
            }
        }
        else if(height>=700) {
            if(width>500) {
                descLength = 600;
                description.setMaxLines(10);
            }else
            {
                descLength = 200;
                description.setMaxLines(6);
            }
        }
*/

        //descHeader.setText(video.getDetail());
        //description.setText(video.getDescription() + " Read More");
        String desc = video.getDescription();
        if (desc.length() > descLength) {
            String temp = desc.substring(0, descLength) + "    ";
            //  messageText.setText((inputText) + Html.fromHtml("  <font color='#4A90E2'>...Read more</font>"));
            String styledText = temp + "<font color='#4C97C4'>...Read more</font>";
            description.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
        } else {
            description.setText(desc);
        }


        UiUtils.log(TAG, "showVideoData: "+video.getTitle() +" "+ video.getDetail() + " "+video.getDescription() );
        if(video.getCasts()!=null && video.getCasts().size()>0)
            starringText.setText(Html.fromHtml(getSpannableCastAndCrew((ArrayList<Cast>) video.getCasts())), TextView.BufferType.SPANNABLE);
        else
            starringText.setVisibility(View.GONE);
        starringText.setHighlightColor(Color.TRANSPARENT);
        starringText.setMovementMethod(LinkMovementMethod.getInstance());

        //director
        //directorText.setText(Html.fromHtml(getSpannableCastAndCrew((ArrayList<Cast>) video.getCasts())), TextView.BufferType.SPANNABLE);
        if(video.getDirector()!=null && !video.getDirector().equalsIgnoreCase(""))
            directorText.setText(String.format("Director : %s", video.getDirector()));
        else
            directorText.setVisibility(View.GONE);
        directorText.setHighlightColor(Color.TRANSPARENT);
        directorText.setMovementMethod(LinkMovementMethod.getInstance());

        //genre
        if(video.getGenresList()!=null && !video.getGenresList().equalsIgnoreCase("")) {

            genreText.setText(String.format("Genre : %s", video.getGenresList().replace(",", " | ")));
            //genreText.setText(Html.fromHtml(getSpannableGenre((ArrayList<GenreSeason>) video.getGenres())), TextView.BufferType.SPANNABLE);
            genreTextBanner.setText(String.format("%s Genre : %s", ParserUtils.parseRating(video.getA_record()), video.getGenresList().replace(",", " | ")));
        }
        else {
            genreText.setVisibility(View.GONE);
            genreTextBanner.setVisibility(View.GONE);
        }
        genreText.setHighlightColor(Color.TRANSPARENT);
        genreText.setMovementMethod(LinkMovementMethod.getInstance());

        exo_txt_genre.setText(video.getGenresList());

        downloadID = video.getDownloadId();
        UiUtils.log(TAG,"Video Status: "+ downloadID);
        Status status = PRDownloader.getStatus(video.getDownloadId());
        UiUtils.log(TAG,"Video Status: "+ status.name());

        showDownloadStatusInViews();
    }

    private void showDownloadStatusInViews() {
        if (video.isDownloadable()) {
            switch (video.getDownloadStatus()) {
                case DO_NOT_SHOW_DOWNLOAD:
                    showNotDownload();
                    break;
                case DOWNLOAD_PROGRESS:
                    showDownloadingButton();
                    break;
                case DOWNLOAD_COMPLETED:
                    showPlayButton();
                    break;
                case SHOW_DOWNLOAD:
                case DOWNLOAD_PAUSED:
                    showDownloadButton(downloadVideoListener);
                    break;
                case NEED_TO_SUBSCRIBE:
                    showDownloadButton(needToSubscribeToDownloadListener);
                    break;
                case NEED_TO_PAY:
                    showDownloadButton(needToPayToDownloadListener);
                    break;
            }
        } else {
            showNotDownload();
        }
    }

    private void setRating(ImageView imgRating,TextView txtRating, int rating)
    {
        if(imgRating!= null) imgRating.setVisibility(View.VISIBLE);
        switch (rating)
        {
            case 1:
                if(imgRating!= null) imgRating.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.rating_a,null));
                if(txtRating!= null) {
                    if (txtRating.getId() != ratingText.getId()) {
                        txtRating.setText(R.string.rating_a_18_video);
                    } else {
                        txtRating.setText("A"/*R.string.rating_a_18*/);
                    }
                }//txtRating.setText(R.string.rating_a_18);
                break;
            case 2:
                if(imgRating!= null) imgRating.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.rating_ua,null));
                if(txtRating!= null) {
                    if (txtRating.getId() != ratingText.getId()) {
                        txtRating.setText(R.string.rating_ua_16_video);
                    } else {
                        txtRating.setText("U/A"/*R.string.rating_ua_16*/);
                    }
                }//txtRating.setText(R.string.rating_ua_16);
                break;
            case 3:
                if(imgRating!= null) imgRating.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.rating_u,null));
                if(txtRating!= null) {
                    if (txtRating.getId() != ratingText.getId()) {
                        txtRating.setText(R.string.rating_u_12_video);
                    } else {
                        txtRating.setText("U"/*R.string.rating_u_12*/);
                    }
                }//txtRating.setText(R.string.rating_u_12);
                break;
            case 4:
                if(imgRating!= null) imgRating.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.rating_kids,null));
                if(txtRating!= null) {
                    if (txtRating.getId() != ratingText.getId()) {
                        txtRating.setText(R.string.rating_kids_video);
                    } else {
                        txtRating.setText("Kids"/*R.string.rating_kids*/);
                    }
                }//txtRating.setText(R.string.rating_kids);
                break;
            default:
                if(imgRating!= null) imgRating.setVisibility(View.GONE);
                if(txtRating!= null) txtRating.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Show resolution chooser dialog
     */
    private void showDownloadResolutionDialogChooser(ArrayList<DownloadUrl> downloadUrls) {
        ArrayList<String> resolutionNames = new ArrayList<>();
        for (int i = 0; i < downloadUrls.size(); i++) {
            resolutionNames.add(downloadUrls.get(i).getTitle());
        }
        String[] resolutions = resolutionNames.toArray(new String[0]);
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle(getString(R.string.choose_download_quality))
                .setSingleChoiceItems(resolutions, 0, (dialog, which) -> {
                    downloadVideo(downloadUrls.get(which).getUrl());
                    dialog.dismiss();
                })
                .setIcon(R.mipmap.ic_launcher)
                .create().show();

    }

    private void downloadVideo(String downloadUrl) {
        UiUtils.log("URL","url --> "+ downloadUrl );
        UiUtils.log("URL","expiry date --> "+ video.getNumDaysToExpire());
        //Toast.makeText(this, downloadUrl, Toast.LENGTH_SHORT).show();

        if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
            PRDownloader.resume(downloadID);
            return;
        }

        Video tempVideo;
        if (video.getIsMain() == 1 && video.getIsSingle() == 0){
            if (!seasonVideos.isEmpty() && 0 < seasonVideos.size()) {
                // Access the element at the specified index
                tempVideo = seasonVideos.get(0);
                String seasonEpisode = "";
                String episodeName = tempVideo.getEpTitle() == null || tempVideo.getEpTitle().equalsIgnoreCase("null") ? "" : tempVideo.getEpTitle();
                if (tempVideo.getSeasonName() != null) {
                    String shortSeason = tempVideo.getSeasonName().length() > 3 ?
                            (String.valueOf(tempVideo.getSeasonName().charAt(0)) + String.valueOf(tempVideo.getSeasonName().charAt(tempVideo.getSeasonName().length() - 1)))
                            : tempVideo.getSeasonName();
                    seasonEpisode = shortSeason + " " + episodeName;
                } else
                    seasonEpisode = episodeName;

                downloadID = NetworkUtils.downloadVideo(this, tempVideo.getAdminVideoId(), tempVideo.getTitle(), seasonEpisode, downloadUrl, video.getNumDaysToExpire());
                showDownloadingButton();
            } else {
                // Handle the case when the ArrayList is empty or the index is out of bounds
                // You can log an error message or take appropriate action here
            }

        }
        else{
            tempVideo = video;
        }


        //downloadView.setVisibility(View.GONE);
        //downloadingView.setVisibility(View.VISIBLE);
    }

    /**
     * Get click spannable text for cast and crew
     * @return
     */
    private String getSpannableCastAndCrew(ArrayList<Cast> casts) {
        starringText.setVisibility(casts.size() == 0 ? View.GONE : View.VISIBLE);
        if (casts.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder(/*"<font color='#BE90A5'>"+*/getString(R.string.cast)+ /*"</font>" + */"      : ");
        builder.append(casts.get(0).getName());
        for (int i = 1; i < casts.size(); i++) {
            builder.append(" | ")
                    .append(casts.get(i).getName());
        }

        String castsString = builder.toString();
/*        SpannableString castStr = new SpannableString(castsString);
        for (int i = 0; i < casts.size(); i++) {
            Cast cast = casts.get(i);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent toCast = new Intent(VideoPageActivity.this, CastVideosActivity.class);
                    toCast.putExtra(CastVideosActivity.CAST_CREW_ID, cast.getId());
                    toCast.putExtra(CastVideosActivity.CAST_CREW_NAME, cast.getName());
                    startActivity(toCast);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            int index = castsString.indexOf(cast.getName());
            int len = cast.getName().length();
            castStr.setSpan(clickableSpan, index, index + len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }*/
        return castsString;
    }

    /**
     * Get click spannable text for cast and crew
     * @return
     */
    private String getSpannableGenre(ArrayList<GenreSeason> genre) {
        genreText.setVisibility(genre.size() == 0 ? View.GONE : View.VISIBLE);
        if (genre.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder(/*"<font color='#BE90A5'>"+*/getString(R.string.genre)+ /*"</font>" + */"      : ");
        builder.append(genre.get(0).getName());
        for (int i = 1; i < genre.size(); i++) {
            builder.append(" | ")
                    .append(genre.get(i).getName());
        }

        String castsString = builder.toString();
/*        SpannableString castStr = new SpannableString(castsString);
        for (int i = 0; i < casts.size(); i++) {
            Cast cast = casts.get(i);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent toCast = new Intent(VideoPageActivity.this, CastVideosActivity.class);
                    toCast.putExtra(CastVideosActivity.CAST_CREW_ID, cast.getId());
                    toCast.putExtra(CastVideosActivity.CAST_CREW_NAME, cast.getName());
                    startActivity(toCast);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            int index = castsString.indexOf(cast.getName());
            int len = cast.getName().length();
            castStr.setSpan(clickableSpan, index, index + len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }*/
        return castsString;
    }


    @OnClick({R.id.shareBtn, R.id.btnTrailer})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shareBtn:
                shareVideoButton();
                break;
            case R.id.btnTrailer:
                if(!isAdultAccepted)
                    showARatedPopup(true);
                else {
                    if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                        startActivity(new Intent(this, NewLoginActivity.class));
                        //startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        //if (!video.getUserType().equalsIgnoreCase("0")) {
                        getUserLoginStatus(VideoPageActivity.this);
                        playTrailer();
                    /*} else {
                        startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                    }*/
                    }
                }
                break;
        }
    }

    public void showARatedPopup(boolean isTrailer)
    {

        //Now we need an AlertDialog.Builder object
        Dialog dialog = new Dialog(activity);

        dialog.setContentView(R.layout.dialog_rating);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.findViewById(R.id.btnYes).setOnClickListener((View v) -> {
            dialog.dismiss();
            //btnSettings.setVisibility(View.VISIBLE);
            //btnSubtitles.setVisibility(View.VISIBLE);
            isAdultAccepted = true;
            if(isTrailer) {
                if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                    startActivity(new Intent(this, NewLoginActivity.class));
                    //startActivity(new Intent(this, LoginActivity.class));
                } else {
                    //if (!video.getUserType().equalsIgnoreCase("0")) {
                    getUserLoginStatus(VideoPageActivity.this);
                    playTrailer();
                    /*} else {
                        startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                    }*/
                }
            }else
            {
                switch (video.getVideoType()) {
                    case VIDEO_MANUAL:
                    case VIDEO_OTHER:
                        /*if (video.isPayPerView()) {
                            if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                                startActivity(new Intent(this, NewLoginActivity.class));
                            } else {
                                if (!video.getUserType().equalsIgnoreCase("0")) {
                                    UiUtils.log(TAG, "playMainVideo:= "+ video.getVideoUrl());
                                    startExoplayer(video.getVideoUrl());
                                    rlPlayer.setVisibility(View.VISIBLE);
                                    rlThumbnail.setVisibility(View.GONE);
                                } else {
                                    startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                                }
                            }

                        } else {*/
                        if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                            startActivity(new Intent(this, NewLoginActivity.class));
                        } else {
                            /*if(video.getIsMain()==1)
                            {
                                getUserLoginStatus(VideoPageActivity.this);
                                playTrailer();
                            }
                            else*/ if (!video.getUserType().equalsIgnoreCase("0")) {
                                UiUtils.log(TAG, "playMainVideo:= "+ video.getVideoUrl());
                                getUserLoginStatus(VideoPageActivity.this);
                                startExoplayer(video.getVideoUrl(), false, MAIN);
                                rlPlayer.setVisibility(View.VISIBLE);
                                rlThumbnail.setVisibility(View.GONE);
                                Log.d("onVideoPlayClickedr", "onVideoPlayClicked: 3"+video.getVideoUrl());
                            } else {
                                startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                            }
                        }
                        //}
                        break;
                    /*case VIDEO_YOUTUBE:
                        if (video.isPayPerView()) {
                            switch (video.getPayPerViewType()) {
                                case ONE:
                                    showPPVSheet();
                                    break;

                                case TWO:
                                    choosePlanDialog();
                            }
                        } else {
                            if (!video.getUserType().equalsIgnoreCase("0")) {
                                Intent toYouTubePlayer = new Intent(this, YouTubePlayerActivity.class);
                                toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_ID, video.getAdminVideoId());
                                toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_URL, video.getVideoUrl());
                                this.startActivity(toYouTubePlayer);
                            } else {
                                startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                            }
                        }
                        break;*/
                }
            }
        });
        dialog.findViewById(R.id.btnNo).setOnClickListener((View v) -> {
            dialog.dismiss();
        });


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void playTrailer() {
        ArrayList<Video> trailerVideos = video.getTrailerVideos();

        if (video.getUserType().equalsIgnoreCase("1") || video.getIsTrailerFree()==1) {
            UiUtils.log(TAG, "playTrailer:= "+ video.getTrailerVideo());
        /*    Intent toPlayer = new Intent(this, ConfigParser.getConfig(null).isRedirectGiraffePlayer() ? GiraffePlayerActivity.class : GiraffePlayerActivity.class); //PlayerActivity
            toPlayer.putExtra(PlayerActivity.VIDEO_ID, video.getAdminVideoId());
            toPlayer.putExtra(PlayerActivity.VIDEO_URL,  video.getVideoUrl());
            toPlayer.putExtra(PlayerActivity.VIDEO_ELAPSED, video.getSeekHere());
            toPlayer.putExtra(PlayerActivity.VIDEO_SUBTITLE, video.getSubTitleUrl());
            this.startActivity(toPlayer);*/
            startExoplayer(video.getTrailerVideo(), true, TRAILER);
            rlPlayer.setVisibility(View.VISIBLE);
            rlThumbnail.setVisibility(View.GONE);

            Log.d("onVideoPlayClickedtywd", "TrailerVideos: Final  "+video.getTrailerVideos().toString());


        }else {
            startActivity(new Intent(getApplicationContext(), PlansActivity.class));
        }
    }


    /**
     * Share video info through text
     */
    private void shareVideoButton() {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(video.getAdminVideoId()));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Url");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,  getString(R.string.check_out_this_video) + video.getShareMessage()
                + "\n\n" + video.getShareUrl());
        startActivity(shareIntent);
    }


    @OnClick({R.id.playVideoBtn, R.id.videoThumbnail})
    public void onVideoPlayClicked() {
        try {
            if(!isAdultAccepted)
                showARatedPopup(false);
            else {
            /*JSONObject jsonObject = new JSONObject(prefUtils.getStringValue(VIDEO_AGE_VERIFICATION,"{}"));
            if(PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false) && !jsonObject.has(String.valueOf(id) + String.valueOf(video.getAdminVideoId())))
            {
                showWarning(video);
            }else {*/
                switch (video.getVideoType()) {
                    case VIDEO_MANUAL:
                    case VIDEO_OTHER:
                        if (video.isPayPerView()) {
                            if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                                startActivity(new Intent(this, NewLoginActivity.class));
                                //startActivity(new Intent(this, LoginActivity.class));
                            } else {
                                /*if(video.getIsMain()==1)
                                {
                                    getUserLoginStatus(VideoPageActivity.this);
                                    playTrailer();
                                }
                                else*/ if (!video.getUserType().equalsIgnoreCase("0")) {
                            /*Intent toPlayer = new Intent(this, ConfigParser.getConfig(null).isRedirectGiraffePlayer() ? GiraffePlayerActivity.class : GiraffePlayerActivity.class); //PlayerActivity
                            toPlayer.putExtra(PlayerActivity.VIDEO_ID, video.getAdminVideoId());
                            toPlayer.putExtra(PlayerActivity.VIDEO_URL, video.getVideoUrl());
                            toPlayer.putExtra(PlayerActivity.VIDEO_ELAPSED, video.getSeekHere());
                            toPlayer.putExtra(PlayerActivity.VIDEO_SUBTITLE, video.getSubTitleUrl());
                            this.startActivity(toPlayer);*/
                                    getUserLoginStatus(VideoPageActivity.this);
                                    startExoplayer(video.getVideoUrl(), false, MAIN);
                                    rlPlayer.setVisibility(View.VISIBLE);
                                    rlThumbnail.setVisibility(View.GONE);

                                    Log.d("onVideoPlayClickedty", "onVideoPlayClicked: 1"+video.getVideoUrl());
                                    //showAgePicker();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                                }
                            }
                        /*switch (video.getPayPerViewType()) {
                            case ONE:
                                showPPVSheet();
                                break;
                            case TWO:
                                choosePlanDialog();
                                break;
                        }*/
                        } else {
                            if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
                                startActivity(new Intent(this, NewLoginActivity.class));
                                //startActivity(new Intent(this, LoginActivity.class));
                            } else {
                               /* if(video.getIsMain()==1)
                                {
                                    getUserLoginStatus(VideoPageActivity.this);
                                    playTrailer();
                                }
                                else*/ if (!video.getUserType().equalsIgnoreCase("0")) {
                            /*Intent toPlayer = new Intent(this, ConfigParser.getConfig(null).isRedirectGiraffePlayer() ? GiraffePlayerActivity.class : GiraffePlayerActivity.class); //PlayerActivity
                            toPlayer.putExtra(PlayerActivity.VIDEO_ID, video.getAdminVideoId());
                            toPlayer.putExtra(PlayerActivity.VIDEO_URL, video.getVideoUrl());
                            toPlayer.putExtra(PlayerActivity.VIDEO_ELAPSED, video.getSeekHere());
                            toPlayer.putExtra(PlayerActivity.VIDEO_SUBTITLE, video.getSubTitleUrl());
                            this.startActivity(toPlayer);*/
                                    getUserLoginStatus(VideoPageActivity.this);
                                    startExoplayer(video.getVideoUrl(), false, MAIN);
                                    rlPlayer.setVisibility(View.VISIBLE);
                                    rlThumbnail.setVisibility(View.GONE);

                                    Log.d("onVideoPlayClickedty", "onVideoPlayClicked: 2"+video.getVideoUrl());
                                    //showAgePicker();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                                }
                            }
                        }
                        break;
                    /*case VIDEO_YOUTUBE:
                        if (video.isPayPerView()) {
                            switch (video.getPayPerViewType()) {
                                case ONE:
                                    showPPVSheet();
                                    break;

                                case TWO:
                                    choosePlanDialog();
                            }
                        } else {
                            if (!video.getUserType().equalsIgnoreCase("0")) {
                                Intent toYouTubePlayer = new Intent(this, YouTubePlayerActivity.class);
                                toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_ID, video.getAdminVideoId());
                                toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_URL, video.getVideoUrl());
                                this.startActivity(toYouTubePlayer);
                            } else {
                                startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                            }
                        }
                        break;*/
                }
                //}
            }
        } catch (Exception e) {
            UiUtils.log(TAG, Log.getStackTraceString(e));
        }
    }

    public void showAgePicker()
    {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_age_selection, viewGroup, false);
        dialogView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show spam dialog by getting reasons and do call <code>addToSpamInBackend</code>.
     */
    private void showSpamDialog() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.getSpamReasons(APIConstants.APIs.GET_SPAM_REASONS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject spamReasonsResponse = null;
                try {
                    spamReasonsResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (spamReasonsResponse != null) {
                    if (spamReasonsResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        JSONArray data = spamReasonsResponse.optJSONArray(DATA);
                        CharSequence[] reasons = null;
                        if (data != null && data.length() > 0) {
                            reasons = new CharSequence[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.optJSONObject(i);
                                reasons[i] = obj.optString(Params.VALUE);
                            }
                        }
                        if (reasons != null && reasons.length > 0) {
                            CharSequence[] finalReasons = reasons;
                            new AlertDialog.Builder(VideoPageActivity.this)
                                    .setTitle(getString(R.string.reason_to_report))
                                    .setItems(reasons, (dialogInterface, i) -> addToSpamInBackend(finalReasons[i].toString()))
                                    .create().show();
                        } else {
                            addToSpamInBackend("Nothing");
                        }
                    } else {
                        UiUtils.showShortToast(VideoPageActivity.this, spamReasonsResponse.optString(ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }


    /**
     * Backend add video to spam API with Spam reason obtained from reasons dialog
     */
    private void addToSpamInBackend(String spamReason) {
        UiUtils.showLoadingDialog(this);
        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, id);
        params.put(Params.TOKEN, prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        params.put(Params.SUB_PROFILE_ID, prefUtils.getIntValue(PrefKeys.ACTIVE_SUB_PROFILE, 0));
        params.put(ADMIN_VIDEO_ID, video.getAdminVideoId());
        params.put(Params.REASON, spamReason);
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));

        Call<String> call = apiInterface.addToSpam(APIConstants.APIs.ADD_TO_SPAM, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject addToSpamResponse = null;
                try {
                    addToSpamResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (addToSpamResponse != null) {
                    if (addToSpamResponse.optString(SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.showShortToast(VideoPageActivity.this, addToSpamResponse.optString(MESSAGE));
                        finish();
                    } else {
                        UiUtils.showShortToast(VideoPageActivity.this, addToSpamResponse.optString(ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }


    /**
     * Use this to show download button along with listeners
     */
    public void showDownloadButton(View.OnClickListener listener) {
//        if (!PrefUtils.getInstance(this).getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
//            downloadView.setVisibility(View.GONE);
//        }else if (!video.getUserType().equalsIgnoreCase("0")) {
//            Log.d("onVideoPlayClicked", "onVideoPlayClicked: 0001");
//            downloadView.setVisibility(View.VISIBLE);
//        }else{
//            Log.d("onVideoPlayClicked", "onVideoPlayClicked: 0002");
//            downloadView.setVisibility(View.GONE);
//        }
        ll_downloadingView.setVisibility(View.GONE);
        ll_downloadedView.setVisibility(View.GONE);
        downloadView.setOnClickListener(listener);
    }

    @Override
    public void onPaymentSucceeded(Invoice invoice) {
    }

    @Override
    public void onPaymentFailed(String failureReason) {

    }

    /*   *//**
     * Use this to show play button along with listeners
     */
    public void showPlayButton() {
        downloadView.setVisibility(View.GONE);
        ll_downloadingView.setVisibility(View.GONE);
        ll_downloadedView.setVisibility(View.VISIBLE);

        playDownloadedView.setOnClickListener(v -> playVideoInOffline());

        deleteDownloadedView.setOnClickListener(v -> {


            Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.dialog_delete_popup);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView txtYes = dialog.findViewById(R.id.btnYes);
            TextView txtNo = dialog.findViewById(R.id.btnNo);

            txtNo.setOnClickListener((View view) -> {
                dialog.dismiss();
            });

            txtYes.setOnClickListener((View view) -> {
                dialog.dismiss();
                //handle delete action click
                try {
                    showDownloadButton(downloadVideoListener);
                    if(video.getIsMain()==1 && video.getIsSingle() ==0)
                        Downloader.downloadDeleted(this, seasonVideos.get(0).getAdminVideoId(), video.getDownloadId());
                    else
                        Downloader.downloadDeleted(this, video.getAdminVideoId(), video.getDownloadId());
                } catch (Exception e) {
                    UiUtils.log(TAG,Log.getStackTraceString(e));
                    UiUtils.showShortToast(this, "Something went wrong. Please try again!");
                }
            });

            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        });
    }



    private void playVideoInOffline() {
        try {

            String fileUri="";
            if(video.getIsMain()==1 && video.getIsSingle() ==0)
                fileUri = DownloadUtils.getVideoPath(this, seasonVideos.get(0).getAdminVideoId(), video.getVideoUrl());
            else
                fileUri = DownloadUtils.getVideoPath(this, video.getAdminVideoId(), video.getVideoUrl());
            Log.d("onVideoPlayClickedty", "playVideoInOffline: Dowmload"+video.getVideoUrl());
            Uri videoUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".myprovider", new File(fileUri));
            if (isValidVideoFile(this, videoUri)) {
                /*startExoplayer(String.valueOf(videoUri));
                rlPlayer.setVisibility(View.VISIBLE);
                rlThumbnail.setVisibility(View.GONE);*/
                //Intent toVideo = new Intent(this, GiraffePlayerActivity.class);
                Intent toVideo = new Intent(this, OfflineVideoActivity.class);
                toVideo.putExtra(ADMIN_VIDEO_ID, video.getAdminVideoId());
                toVideo.putExtra(VIDEO_URL, String.valueOf(videoUri));
                toVideo.putExtra(VIDEO_ELAPSED, video.getSeekHere());
                toVideo.putExtra(SUBTITLE_URL, video.getSubTitleUrl());
                startActivity(toVideo);
            } else {
                UiUtils.showShortToast(this, getString(R.string.problem_with_downloaded_video));
                showDownloadButton(downloadVideoListener);
                if(video.getIsMain()==1 && video.getIsSingle() ==0)
                    Downloader.downloadDeleted(this, seasonVideos.get(0).getAdminVideoId(), video.getDownloadId());
                else
                    Downloader.downloadDeleted(this, video.getAdminVideoId(), video.getDownloadId());
            }
        } catch (Exception e) {
            UiUtils.log(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Use this to not show download button
     */
    private void showNotDownload() {
        downloadView.setVisibility(View.GONE);
        ll_downloadingView.setVisibility(View.GONE);
        ll_downloadedView.setVisibility(View.GONE);
    }

    /**
     * Use this to show downloading button along with listeners
     */
    public void showDownloadingButton() {
        ll_downloadingView.setVisibility(View.VISIBLE);
        downloadView.setVisibility(View.GONE);
        ll_downloadedView.setVisibility(View.GONE);

        pauseDownloadingView.setOnClickListener( v-> {
            UiUtils.log(TAG,"Video Paused: "+  PRDownloader.getStatus(downloadID));
            if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
                UiUtils.log(TAG,"Video Paused");
                PRDownloader.pause(downloadID);
                showDownloadButton(downloadVideoListener);
            }
        });

        cancelDownloadingView.setOnClickListener(v->{
            showDownloadButton(downloadVideoListener);
            if(video.getIsMain()==1 && video.getIsSingle() ==0)
                Downloader.downloadCanceled(this, seasonVideos.get(0).getAdminVideoId(), video.getDownloadId());
            else
                Downloader.downloadCanceled(this, video.getAdminVideoId(), video.getDownloadId());
            PRDownloader.cancel(downloadID);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(downloadID);
        });
    }

    @Override
    public void downloadCompleted(int adminVideoId) {
        showPlayButton();
    }

    @Override
    public void downloadCancelled(int adminVideoId) {
        showDownloadStatusInViews();
    }

    @Override
    public void downloadPaused(int adminVideoId) {
        showDownloadButton(downloadVideoListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String paymentId;
        if (requestCode == PAY_PAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        UiUtils.log(TAG,paymentDetails);
                        JSONObject details = new JSONObject(paymentDetails);
                        paymentId = details.optJSONObject(RESPONSE).optString(ID);
                        sendPayPalPaymentToBackend(paymentId);
                    } catch (JSONException e) {
                        UiUtils.log(TAG, Log.getStackTraceString(e));
                    } catch (Exception e) {
                        UiUtils.log(TAG, Log.getStackTraceString(e));
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                onPaymentFailed(getString(R.string.thePaymentHasBeenCancelled));
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            onPaymentFailed(getString(R.string.invalid_payment));
        }
    }

    private void sendPayPalPaymentToBackend(String paymentId) {
        UiUtils.showLoadingDialog(this);
        Map<String, Object> params = new HashMap<>();
        params.put(Params.ID, id);
        params.put(Params.TOKEN, token);
        params.put(Params.SUB_PROFILE_ID, subProfileId);
        params.put(ADMIN_VIDEO_ID, invoice.getVideo().getAdminVideoId());
        params.put(Params.PAYMENT_ID, paymentId);
        params.put(Params.COUPON_CODE, invoice.getCouponCode());
        params.put(Params.LANGUAGE, prefUtils.getStringValue(PrefKeys.APP_LANGUAGE_STRING, ""));
        params.put(Params.IP, CURRENT_IP);

        Call<String> call = apiInterface.makePayPalPPV(APIConstants.APIs.MAKE_PAYPAL_PPV,params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject paymentObject = null;
                try {
                    paymentObject = new JSONObject(response.body());
                } catch (JSONException e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                } catch (Exception e) {
                    UiUtils.log(TAG, Log.getStackTraceString(e));
                }
                if (paymentObject != null) {
                    if (paymentObject.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        Toast.makeText(VideoPageActivity.this, paymentObject.optString(MESSAGE), Toast.LENGTH_SHORT).show();
                        invoice.setPaymentId(paymentId);
                        onPaymentSucceeded(invoice);
                    } else {
                        UiUtils.hideLoadingDialog();
                        Toast.makeText(VideoPageActivity.this, paymentObject.optString(ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.hideLoadingDialog();
                NetworkUtils.onApiError(VideoPageActivity.this);
            }
        });
    }

    @Override
    public void onMakePayPalPayment(Invoice invoice) {
        this.invoice = invoice;
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(invoice.getPaidAmount())), invoice.getCurrencySymbol(), invoice.getTitle(),
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAY_PAL_REQUEST_CODE);
    }

    @Override
    public void onDataChanged() {

    }
/////////////////////////////////////////////startExoplayer
    void startExoplayer(String fileUrl, boolean isTrailer, String videoType) {

        {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(video.getAdminVideoId()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT, fileUrl);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, videoType);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            trailerPlaying = isTrailer;
            toolbar.setVisibility(View.GONE);
            rl_video_title.setVisibility(View.VISIBLE);
            UiUtils.log(TAG, "Started");
            currentlyStreamingUrl = fileUrl;
            playbackPosition = 3;

//            if (parentMediaContent.getTrailerFileUrl() != null && FileUrl != null && FileUrl.contains(parentMediaContent.getTrailerFileUrl())) {
//                isTrailerPlaying = true;
//            } else {
//                isTrailerPlaying = false;
//            }


            String cookieValue = "CloudFront-Policy="+COOKIEPOLICY+
                    ";CloudFront-Signature="+COOKIESIGNATURE+
                    ";CloudFront-Key-Pair-Id="+COOKIEKEYPAIR;

            HashMap<String,String> headers = new HashMap<>();
            headers.put("Cookie",cookieValue);

            String userAgent = getUserAgent(this, getApplicationContext().getApplicationInfo().packageName);
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
            httpDataSourceFactory.setUserAgent(userAgent);
            httpDataSourceFactory.setDefaultRequestProperties(headers);
            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this, httpDataSourceFactory);

            //SubtitleConfig
            MediaItem.SubtitleConfiguration subtitleConfig = createSubtitleConfig(isTrailer);

            //Media Item
            MediaItem mediaItem = createMediaItem(fileUrl,subtitleConfig);

            if(getFileExtension(fileUrl).equalsIgnoreCase("m3u8"))
            {
                UiUtils.log(TAG, "HlsMediaSource");
                mVideoSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .setExtractorFactory(HlsExtractorFactory.DEFAULT)
                        .createMediaSource(mediaItem);
            }else
            {
                UiUtils.log(TAG, "ProgressiveMediaSource");
                mVideoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);
            }

            ArrayList<MediaSource> combinedMediaSources = new ArrayList<>();
            combinedMediaSources.add(mVideoSource);


            //Multiple Videos
            UiUtils.log(TAG, "Video Language->" + video.getVideoLanguageUrl().size());
            UiUtils.log(TAG, "Trailer Language->" + video.getTrailerVideoLanguageUrl().size());
            timers = new ArrayList<>();
            rl_exo_link.setVisibility(View.GONE);
            if(isTrailer) {
                ArrayList<MediaSource> trailerMediaSources = addLanguageMediaItem(video.getTrailerVideoLanguageUrl(), dataSourceFactory, subtitleConfig);
                if(trailerMediaSources.size()>0)
                    combinedMediaSources.addAll(trailerMediaSources);


                if(!checkString(video.getRatingTrailerTime()))
                    timers.addAll(Arrays.asList(video.getRatingTrailerTime().split(",")));

                //if(!(checkString(video.getRatingTrailerUrl()) || checkString(video.getRatingTrailerType())))
                    rl_exo_link.setVisibility(View.GONE);
            }else
            {
                ArrayList<MediaSource> videoMediaSources = addLanguageMediaItem(video.getVideoLanguageUrl(), dataSourceFactory, subtitleConfig);
                if(videoMediaSources.size()>0)
                    combinedMediaSources.addAll(videoMediaSources);

                if(!checkString(video.getRatingTime()))
                    timers.addAll(Arrays.asList(video.getRatingTime().split(",")));

                if(!(checkString(video.getRatingUrl()) || checkString(video.getRatingType())))
                    rl_exo_link.setVisibility(View.VISIBLE);
            }

            if(subtitleConfig!=null) {
                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(subtitleConfig, C.TIME_UNSET);
                combinedMediaSources.add(subtitleSource);
                btnSubtitles.setVisibility(View.VISIBLE);
            }else
            {
                //btnSubtitles.setVisibility(View.GONE);
            }

            rl_exo_link.setOnClickListener(v -> {
                new ElasticAnimation(v).setScaleX(0.85f).setScaleY(0.85f).setDuration(300)
                        .setOnFinishListener(new ElasticFinishListener() {
                            @Override
                            public void onFinished() {
                                // Do something after duration time
                                String type = isTrailer? video.getRatingTrailerType():video.getRatingType();
                                switch (type.toLowerCase()) {
                                    case "media":
                                        Intent i = new Intent(VideoPageActivity.this, VideoPageActivity.class);
                                        i.putExtra(ADMIN_VIDEO_ID, Integer.parseInt(video.getRatingUrl()));
                                        i.putExtra(PARENT_ID, Integer.parseInt(video.getRatingUrl()));
                                        startActivity(i);
                                        finish();
                                        break;
                                     /*case "url":
                                         Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).getParent_media()));
                                         context.startActivity(browserIntent);
                                         break;*/
                                     /*case "youtube":
                                         Intent toYouTubePlayer = new Intent(context, YouTubePlayerActivity.class);
                                         toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_ID, data.get(position).getAdminVideoId());
                                         toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_URL, "https://youtu.be/VLTwnY7wY0Y");
                                                 context.startActivity(toYouTubePlayer);
                                         break;*/
                                    default:
                                        try {
                                            if(!checkString(video.getRatingUrl())) {
                                                Intent browserIntentDefault = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getRatingUrl()));
                                                startActivity(browserIntentDefault);
                                            }
                                        }catch(Exception e)
                                        {
                                            Toast.makeText(VideoPageActivity.this, getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                                            UiUtils.log(TAG, Log.getStackTraceString(e));
                                        }
                        /*context.startActivity(new Intent(context, WebViewActivity.class)
                                .putExtra(WebViewActivity.PAGE_TYPE, WebViewActivity.PageTypes.BANNER)
                                .putExtra("URL", "https://youtu.be/VLTwnY7wY0Y"));*/
                                        break;
                                }
                            }
                        }).doAction();
            });

            //every media source becomes is assigned a renderer index , video=0,audio=1,subtitles=2
            //MergingMediaSource combines all the sources in the respective renderers ( all videos goes to renderer ID 0 , audio to 1 and so on)
            mergedSource = new MergingMediaSource(combinedMediaSources.toArray(new MediaSource[0]));

            initExoPlayer();

        }

    }

    private MediaItem.SubtitleConfiguration createSubtitleConfig(boolean isTrailer)
    {
        String subtitle, subtitleLang;
        if(isTrailer) {
            subtitle = video.getTrailerSubtitle();
            subtitleLang = video.getTrailerSubtitleLang();
        }
        else {
            subtitle = video.getSubTitleUrl();
            subtitleLang = video.getSubTitleLang();
        }

        UiUtils.log(TAG,"Subtitle: "+ subtitle);

        if(!checkString(subtitle)) {

            MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitle))
                    .setMimeType(MimeTypes.TEXT_VTT) // The correct MIME type (required).
                    .setLabel(subtitle)
                    .setLanguage(checkString(subtitleLang) ? "English" : subtitleLang) // The subtitle language (optional).
                    .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT) // Selection flags for the track (optional).
                    .build();
            return subtitleConfig;
        }else
            return null;
    }

    private MediaItem createMediaItem(String fileUrl, MediaItem.SubtitleConfiguration subtitleConfig)
    {

        MediaItem mediaItem;
        if(subtitleConfig!=null) {
            mediaItem = new MediaItem.Builder()
                    .setUri(fileUrl)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .setSubtitleConfigurations(ImmutableList.of(subtitleConfig))
                    .build();
        }else {
            mediaItem = new MediaItem.Builder()
                    .setUri(fileUrl)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build();
        }

        return mediaItem;
    }

    private ArrayList<MediaSource> addLanguageMediaItem(List<String> streamUrls,DefaultDataSource.Factory dataSourceFactory, MediaItem.SubtitleConfiguration subtitleConfig)
    {

        ArrayList<MediaSource> extraMediaSource = new ArrayList<>();

        for (int i = 0; i < streamUrls.size(); i++) {
            MediaSource mediaSource = null;

            //Media Item
            MediaItem mediaItemExtra = createMediaItem(streamUrls.get(i), subtitleConfig);
            if (getFileExtension(streamUrls.get(i)).equalsIgnoreCase("m3u8")) {
                UiUtils.log(TAG, "HlsMediaSource");
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .setExtractorFactory(HlsExtractorFactory.DEFAULT)
                        .createMediaSource(mediaItemExtra);
                UiUtils.log(TAG, "Language Hls->" + streamUrls.get(i));
            } else {
                UiUtils.log(TAG, "ProgressiveMediaSource");
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItemExtra);
                UiUtils.log(TAG, "Language Progr->" + streamUrls.get(i));
            }

            extraMediaSource.add(mediaSource);
        }

        return extraMediaSource;
    }

    private void initExoPlayer() {
        //https://stackoverflow.com/questions/42228653/exoplayer-adaptive-hls-streaming
        UiUtils.log(TAG, "initExoPlayer ");
        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(0,0,0,0);
        trackSelector = new DefaultTrackSelector(VideoPageActivity.this, videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();


//        trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
//        subtitlesSelectionHelper = new SubtitlesSelectionHelper(trackSelector, videoTrackSelectionFactory);

        if (player == null) {
            UiUtils.log(TAG, "ExoPlayer");
            player = new ExoPlayer.Builder(this)
                    .setRenderersFactory(new DefaultRenderersFactory(this))
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build();
            mExoPlayerView.setPlayer(player);
            mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        } else {
            UiUtils.log(TAG, "Else ExoPlayer");
            player.stop();
            player.release();

            mExoPlayerView.setPlayer(null);
            player = new ExoPlayer.Builder(this)
                    .setRenderersFactory(new DefaultRenderersFactory(this))
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build();
            mExoPlayerView.setPlayer(player);
            mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_ENDED) {
                    controlView.setVisibility(View.VISIBLE);
                    player.seekTo(0);
                    player.setPlayWhenReady(false);
                    exo_play.setVisibility(View.VISIBLE);
                    AnimatedVectorDrawable animatedVectorDrawable;
                    exo_play.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
                    //AnimationDrawable logoAnimation = (AnimationDrawable) exo_play.getBackground();
                    //logoAnimation.start();
                    Drawable drawable = exo_play.getDrawable();
                    if (drawable instanceof AnimatedVectorDrawable) {
                        animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                        animatedVectorDrawable.start();
                    }
                    //player.pause();
                    /*exo_play.setVisibility(View.VISIBLE);
                    exo_play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            player.play();
                        }
                    });*/

                    timers = new ArrayList<>();
                    if(trailerPlaying)
                    {
                        if(!checkString(video.getRatingTrailerTime()))
                            timers.addAll(Arrays.asList(video.getRatingTrailerTime().split(",")));
                    }else {
                        if(!checkString(video.getRatingTime()))
                            timers.addAll(Arrays.asList(video.getRatingTime().split(",")));
                    }
                    addTimerMessage(timers);

                } else if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);

                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            /*@Override
            public void onCues(List<Cue> cues) {
                Player.Listener.super.onCues(cues);
                if (btnSubtitles != null) {
                    btnSubtitles.setma(cues);
                    btnSubtitles.onCues(cues);
                }
            }*/


            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if(isPlaying)
                {
                    UiUtils.log(TAG,"isPlaying-> "+ isPlaying);
                    soundView.setVisibility(View.VISIBLE);
                    //exoPlayPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_play_arrow_24,null));
                    //exoPlayPause.setImageDrawable(null);
                    exo_play.setVisibility(View.GONE);
                    exo_pause.setVisibility(View.VISIBLE);
                    videoPlaying = true;
                    /*if(!videoPlaying) {
                        AnimatedVectorDrawable animatedVectorDrawable;
                        exo_pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
                        //AnimationDrawable logoAnimation = (AnimationDrawable) exo_play.getBackground();
                        //logoAnimation.start();
                        Drawable drawable = exo_pause.getDrawable();
                        if (drawable instanceof AnimatedVectorDrawable) {
                            animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                            animatedVectorDrawable.start();
                        }
                        videoPlaying = true;
                    }*/
                }else
                {
                    UiUtils.log(TAG,"isPlaying-> "+ isPlaying);
                    //exoPlayPause.setImageDrawable(null);
                    //exoPlayPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.exo_player_pause_btn_new,null));
                    exo_play.setVisibility(View.VISIBLE);
                    exo_pause.setVisibility(View.GONE);
                    videoPlaying = false;
                    /*if(videoPlaying) {
                        AnimatedVectorDrawable animatedVectorDrawable;
                        exo_play.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
                        //AnimationDrawable logoAnimation = (AnimationDrawable) exo_play.getBackground();
                        //logoAnimation.start();
                        Drawable drawable = exo_play.getDrawable();
                        if (drawable instanceof AnimatedVectorDrawable) {
                            animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                            animatedVectorDrawable.start();
                        }
                        videoPlaying = false;
                    }*/
                }
                /*exoPlayPause.setImageResource(R.drawable.anim_play_to_pause);
                AnimationDrawable logoAnimation = (AnimationDrawable) exoPlayPause.getDrawable();
                logoAnimation.start();*/

                /*Animation fadeOut = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.zoom_out);
                exoPlayPause.startAnimation(fadeOut);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Animation fadeIn = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.zoom_in);
                        exoPlayPause.startAnimation(fadeIn);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });*/
               /* }else
                {
                    Animation fadeOut = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.fade_out);
                    exoPlayPause.startAnimation(fadeOut);

                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Animation fadeIn = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.fade_in);
                            exoPlayPause.startAnimation(fadeIn);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }*/
            }

            @Override
            public void onMediaMetadataChanged(com.google.android.exoplayer2.MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);

                UiUtils.log("TextInformationFrame", ""+mediaMetadata.subtitle);

            }
        });

        //mExoPlayerView.setOnTouchListener(null);
        addTimerMessage(timers);

        ArrayList<MediaSource> mergedSources = new ArrayList<>();
        mergedSources.add(mergedSource);

        player.setMediaSources(mergedSources);
        //player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);


    }

    private void addTimerMessage(ArrayList<String> timers)
    {
        //long position = 20 * 1000;
        for (PlayerMessage playerMessage : playerMessages)
        {
            playerMessage.cancel();
        }


        for(int i=0; i<timers.size();i++) {
            Handler handler = new Handler();
            playerMessages.add(player.createMessage(
                            (messageType, payload) -> {
                                Log.e("TAG", "Hurray! We are at playback position 0:20 --> " + player.getCurrentPosition()/1000);
                                mRatingHandler.removeCallbacks(mRatingRunnable);
                                ll_exo_rating.setVisibility(View.VISIBLE);
                                Animation slide_up_fade_in = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.slide_up_fade_in);
                                ll_exo_rating.startAnimation(slide_up_fade_in);
                                slide_up_fade_in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        /*Animation slide_up = AnimationUtils.loadAnimation(VideoPageActivity.this, R.anim.slide_up);
                                        ll_exo_rating.startAnimation(slide_up);*/
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        //ll_exo_rating.setVisibility(View.VISIBLE);
                                        mRatingHandler.postDelayed(mRatingRunnable, 7000);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            })
                    .setPosition(getTime(timers.get(i)))
                    .setHandler(handler)
                    .send()
            );
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {
        controlView.setVisibility(View.GONE);
    }

    @Override
    public void onCastSessionAvailable() {
    }

    @Override
    public void onCastSessionUnavailable() {

    }

    private void showDownloadResolutionDialog(ArrayList<DownloadUrl> downloadUrls) {

        if (!video.getUserType().equalsIgnoreCase("0") || video.getIsTrailerFree()==1) {

            ArrayList<String> resolutionNames = new ArrayList<>();
            if (downloadUrls != null) {
                for (int i = 0; i < downloadUrls.size(); i++) {
                    if(!downloadUrls.get(i).getLanguage().equalsIgnoreCase(""))
                        resolutionNames.add(downloadUrls.get(i).getTitle() +" - "+downloadUrls.get(i).getLanguage());
                    else
                        resolutionNames.add(downloadUrls.get(i).getTitle());
                }
            } else {
                resolutionNames.add("Not Available");
            }

            // Create custom dialog object
            final Dialog dialog = new Dialog(VideoPageActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_download);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // Set dialog title
            //dialog.setTitle("Custom Dialog");

            // set values for custom dialog components - text, image and button
            RadioGroup rg_resolutions = (RadioGroup) dialog.findViewById(R.id.rg_resolutions);
            rg_resolutions.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            rg_resolutions.setDividerDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null));

            //ImageView img_bg = dialog.findViewById(R.id.img_bg);
            UiUtils.log(TAG, "Resolution Size: " + resolutionNames.size());
            //if (resolutionNames.size() < 2)
            //    img_bg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_popup_tnc, null));

            for (int i = 0; i < resolutionNames.size(); i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setPadding(20, 20, 20, 20);
                rdbtn.setGravity(Gravity.RIGHT);
                if (resolutionNames.get(i).equalsIgnoreCase(ORIGINAL))
                    rdbtn.setText(resolutionNames.get(i));
                else
                    rdbtn.setText(resolutionNames.get(i)/*getQuality(resolutionNames.get(i))*/);
                rdbtn.setTextColor(getResources().getColor(R.color.white));
                rdbtn.setTextSize(18f);
                rdbtn.setPadding(20, -5, 0, 0);
                rdbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!resolutionNames.get(0).equalsIgnoreCase("Not Available"))
                            downloadVideo(downloadUrls.get(rdbtn.getId()).getUrl());
                        dialog.dismiss();
                    }
                });
                rg_resolutions.addView(rdbtn);
            }

            dialog.show();
        }
        else
            startActivity(new Intent(getApplicationContext(), PlansActivity.class));

    }

    private void initFullscreenButton() {

        controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.img_exo_fullscreen);
        //btnSubtitles = controlView.findViewById(R.id.exo_subtitle);
        btnSubtitles = controlView.findViewById(R.id.rl_exo_subtitle);
        View btnSettings = controlView.findViewById(R.id.exo_settings);
        //View btnAudio = controlView.findViewById(R.id.exo_audio);
        RelativeLayout btnAudio = controlView.findViewById(R.id.rl_exo_audio);

        rl_exo_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = video.getRatingType();
                switch (type.toLowerCase()) {
                    case "media":
                        Intent i = new Intent(VideoPageActivity.this, VideoPageActivity.class);
                        i.putExtra(ADMIN_VIDEO_ID, Integer.parseInt(video.getRatingUrl()));
                        i.putExtra(PARENT_ID, Integer.parseInt(video.getRatingUrl()));
                        startActivity(i);
                        finish();
                        break;
                    /*case "url":
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).getParent_media()));
                        context.startActivity(browserIntent);
                        break;*/
                    /*case "youtube":
                        Intent toYouTubePlayer = new Intent(context, YouTubePlayerActivity.class);
                        toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_ID, data.get(position).getAdminVideoId());
                        toYouTubePlayer.putExtra(YouTubePlayerActivity.VIDEO_URL, "https://youtu.be/VLTwnY7wY0Y");
                                context.startActivity(toYouTubePlayer);
                        break;*/
                    default:
                        try {
                            if(!checkString(video.getRatingUrl())) {
                                Intent browserIntentDefault = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getRatingUrl()));
                                startActivity(browserIntentDefault);
                            }
                        }catch(Exception e)
                        {
                            Toast.makeText(VideoPageActivity.this, getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                            UiUtils.log(TAG, Log.getStackTraceString(e));
                        }
                        /*context.startActivity(new Intent(context, WebViewActivity.class)
                                .putExtra(WebViewActivity.PAGE_TYPE, WebViewActivity.PageTypes.BANNER)
                                .putExtra("URL", "https://youtu.be/VLTwnY7wY0Y"));*/
                        break;
                }
            }
        });

        btnSubtitles.setOnClickListener(view -> showSubtitlesSelectionDialog(view));
        btnSettings.setOnClickListener(view -> showQualitySelectionDialog(view));
        btnAudio.setOnClickListener(view -> showAudioSelectionDialog(view));

        //Forward & Rewind
        ImageButton exo_ffwd11 = controlView.findViewById(R.id.exo_ffwd);
        ImageButton exo_rew11 = controlView.findViewById(R.id.exo_rew);
        exo_ffwd11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
                exo_ffwd11.startAnimation(aniRotate);
                player.seekTo(player.getCurrentPosition()+10000);
            }
        });
        exo_rew11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
                exo_rew11.startAnimation(aniRotate);
                player.seekTo(player.getCurrentPosition()-10000);
            }
        });

        mFullScreenButton = controlView.findViewById(R.id.rl_exo_fullscreen);
        //mFullScreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand));
        mFullScreenButton.setVisibility(View.VISIBLE);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog(true);
                else
                    closeFullscreenDialog(true);
            }
        });

        exo_play = controlView.findViewById(R.id.exo_play);
        exo_pause = controlView.findViewById(R.id.exo_pause);
        exo_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.play();
                //if(!videoPlaying) {
                AnimatedVectorDrawable animatedVectorDrawable;
                exo_pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
                Drawable drawable = exo_pause.getDrawable();
                if (drawable instanceof AnimatedVectorDrawable) {
                    animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                    animatedVectorDrawable.start();
                }
                videoPlaying = true;
                //}
            }
        });
        exo_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
                //if(videoPlaying) {
                AnimatedVectorDrawable animatedVectorDrawable;
                exo_play.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
                Drawable drawable = exo_play.getDrawable();
                if (drawable instanceof AnimatedVectorDrawable) {
                    animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
                    animatedVectorDrawable.start();
                }
                videoPlaying = false;
                //}
            }
        });

        exoLock = controlView.findViewById(R.id.exo_lock);
        exoLock.setVisibility(View.GONE);
        exoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtils.log(TAG,"Lock Clicked");
                /*if(mExoPlayerIsLocked)
                {
                    mExoPlayerView.showController();
                    mExoPlayerView.setUseController(true);
                    //exoLock.setColorFilter(ContextCompat.getColor(VideoPageActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    exoUnLock.setVisibility(View.GONE);
                    mExoPlayerIsLocked = false;
                    mLockHandler.removeCallbacks(mLockRunnable);
                    mLockHandler.postDelayed(mLockRunnable, 2000);
                }else {*/
                mExoPlayerView.hideController();
                mExoPlayerView.setUseController(false);
                //exoUnLock.setColorFilter(ContextCompat.getColor(VideoPageActivity.this, R.color.colorPrimaryHeaderPink), android.graphics.PorterDuff.Mode.SRC_IN);
                exoUnLock.setVisibility(View.VISIBLE);
                mExoPlayerIsLocked = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                mLockHandler.removeCallbacks(mLockRunnable);
                mLockHandler.postDelayed(mLockRunnable, 2000);
                //}
            }
        });

        exoUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExoPlayerView.showController();
                mExoPlayerView.setUseController(true);
                //exoLock.setColorFilter(ContextCompat.getColor(VideoPageActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                mLockHandler.removeCallbacks(mLockRunnable);
                exoUnLock.setVisibility(View.GONE);
                mExoPlayerIsLocked = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        });

        soundView.setOnsoundProgressChangeListner(new SoundProgressChangeListner() {
            @Override
            public void onchange(int progress) {
                final float adjust = progress / 15f;
                UiUtils.log("Touch", "Volume: " + progress);
                UiUtils.log("Touch", "Adjust: " + adjust);
                player.setVolume(adjust);
                progress_volume_text.setText(String.valueOf(progress));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                if(progress<=0)
                    volumeicon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_vol_mute,null));
                else
                    volumeicon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_vol_unmute,null));

                mVolHandler.removeCallbacks(mVolRunnable);
                mVolHandler.postDelayed(mVolRunnable, 2000);
            }
        });
        brightnessView.setOnsoundProgressChangeListner(new SoundProgressChangeListner() {
            @Override
            public void onchange(int progress) {
                final float adjust = progress / 100;
                UiUtils.log("Touch", "Brightness: " + progress);
                mBrightHandler.removeCallbacks(mBrightRunnable);
                mBrightHandler.postDelayed(mBrightRunnable, 2000);
            }
        });
    }

    //video quality selection
    private void showQualitySelectionDialog(View view) {

        if(!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false,0, true);
            //trackSelectionDialog.getView().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
        }
    }

    //Audio quality selection
    private void showAudioSelectionDialog(View view) {

        if(!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false, 1, true);
            //trackSelectionDialog.getView().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
        }
    }

    //Speed quality selection
    private void showSpeedSelectionDialog(View view) {

        // Create custom dialog object
        final Dialog dialog = new Dialog(VideoPageActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_download);
        // Set dialog title
        //dialog.setTitle("Custom Dialog");

        final float[] PLAYBACK_SPEEDS =
                new float[] {0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f};


        // set values for custom dialog components - text, image and button
        RadioGroup rg_resolutions = (RadioGroup) dialog.findViewById(R.id.rg_resolutions);
        rg_resolutions.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        rg_resolutions.setDividerDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_drawable, null));

        UiUtils.log(TAG, "Resolution Size: " + PLAYBACK_SPEEDS.length);
        //if (resolutionNames.size() < 2)
        //    img_bg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.popup_bg_smallest, null));

        for (int i = 0; i < PLAYBACK_SPEEDS.length; i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(i);
            rdbtn.setPadding(20, 20, 20, 20);
            rdbtn.setGravity(Gravity.RIGHT);
            rdbtn.setText(String.valueOf(PLAYBACK_SPEEDS[i]));
            rdbtn.setTextColor(getResources().getColor(R.color.white));
            rdbtn.setTextSize(18f);
            rdbtn.setPadding(20, -5, 0, 0);
            if(playbackPosition==i)
                rdbtn.setChecked(true);
            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = rg_resolutions.getCheckedRadioButtonId();

                    player.setPlaybackSpeed(PLAYBACK_SPEEDS[selectedId]);
                    playbackPosition = selectedId;
                    //player.setVolume(PLAYBACK_VOLUME[selectedId]);
                    dialog.dismiss();
                }
            });
            rg_resolutions.addView(rdbtn);
        }

        dialog.show();
    }

    //subtitles change
    private void showSubtitlesSelectionDialog(View view) {

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            if(!isShowingTrackSelectionDialog
                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector,
                                /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false,2, true);
                trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);

            }
        }
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog(true);
                super.onBackPressed();
            }

            @Override
            public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                    volumecontainerView.setVisibility(View.VISIBLE);
                    UiUtils.log(TAG,"touch volume down -> "+ audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    int newStream = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)-1;
                    soundView.setProgress(newStream);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    if (mExoPlayerFullscreen && !mExoPlayerIsLocked)
                        closeFullscreenDialog(true);
                }

                return  true;
                //return super.onKeyDown(keyCode, event);
            }

            @Override
            public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
                    volumecontainerView.setVisibility(View.VISIBLE);
                    UiUtils.log(TAG,"touch volume up -> "+ audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    int newStream = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+1;
                    soundView.setProgress(newStream);
                }

                return  true;
                //return super.onKeyUp(keyCode, event);
            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (!mExoPlayerFullscreen)
            openFullscreenDialog(false);
        else
            closeFullscreenDialog(false);*/
        UiUtils.log(TAG,"New Config : "+ newConfig.orientation + " --> "+Configuration.ORIENTATION_LANDSCAPE);
        //Toast.makeText(this, "New Config : "+ newConfig.orientation + " --> "+Configuration.ORIENTATION_LANDSCAPE, Toast.LENGTH_LONG).show();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!mExoPlayerFullscreen)
                openFullScreenUiChange();
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(!mExoPlayerIsLocked)
                closeFullScreenUiChange();
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFullscreenDialog(boolean change) {
        try {
            if(change) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                reActivateOrientationDetection();
            }
            /*((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_skrink));
            mExoPlayerFullscreen = true;
            mFullScreenDialog.show();*/
        }catch (Exception ex){ex.printStackTrace();}
    }

    private void openFullScreenUiChange()
    {
        try {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mExoPlayerView.setPlayer(player);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_ic_fullscreen_exit));
            mExoPlayerFullscreen = true;
            volumecontainerView.setVisibility(View.VISIBLE);
            brightnesscontainerView.setVisibility(View.VISIBLE);
            exoLock.setVisibility(View.VISIBLE);
            exo_txt_rating.setTextSize(14);
            exo_txt_genre.setTextSize(12);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ll_exo_rating.getLayoutParams();
            params.setMargins(Utils.dpToPx(30),Utils.dpToPx(20),0,0);
            ll_exo_rating.setLayoutParams(params);
            rl_exo_link.setGravity(Gravity.END | Gravity.BOTTOM);
            FrameLayout.LayoutParams paramsNew = (FrameLayout.LayoutParams) rl_exo_link.getLayoutParams();
            paramsNew.setMargins(0,0,0,Utils.dpToPx(40));
            paramsNew.setMarginEnd(Utils.dpToPx(25));
            rl_exo_link.setLayoutParams(paramsNew);
            playerTouch();
            if(ll_exo_rating.getVisibility() == View.VISIBLE)
            {
                mRatingHandler.removeCallbacks(mRatingRunnable);
                mRatingHandler.postDelayed(mRatingRunnable, 5000);
            }
            mFullScreenDialog.show();

        }catch (Exception ex){ex.printStackTrace();}
    }

    private void closeFullscreenDialog(boolean change) {
        try {
            if(change) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                reActivateOrientationDetection();
            }
            /*((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
            mExoPlayerFullscreen = false;
            ((RelativeLayout) findViewById(R.id.rlPlayer)).addView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenDialog.dismiss();
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand));*/
        }catch (Exception ex){ex.printStackTrace();}
    }

    private void closeFullScreenUiChange()
    {
        try {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mExoPlayerView.setPlayer(player);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
            mExoPlayerFullscreen = false;
            //exoBackBtn.setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.rlPlayer)).addView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenDialog.dismiss();
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.exo_ic_fullscreen_enter));
            volumecontainerView.setVisibility(View.GONE);
            brightnesscontainerView.setVisibility(View.GONE);
            exoUnLock.setVisibility(View.GONE);
            exoLock.setVisibility(View.GONE);
            exo_txt_rating.setTextSize(12);
            exo_txt_genre.setTextSize(10);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ll_exo_rating.getLayoutParams();
            params.setMargins(Utils.dpToPx(16),Utils.dpToPx(16),0,0);
            ll_exo_rating.setLayoutParams(params);
            rl_exo_link.setGravity(Gravity.END | Gravity.BOTTOM);
            FrameLayout.LayoutParams paramsNew = (FrameLayout.LayoutParams) rl_exo_link.getLayoutParams();
            paramsNew.setMargins(0,0,0,Utils.dpToPx(40));
            paramsNew.setMarginEnd(Utils.dpToPx(8));
            rl_exo_link.setLayoutParams(paramsNew);
            mExoPlayerIsLocked=false;
            mExoPlayerView.setOnTouchListener(null);
            if(ll_exo_rating.getVisibility() == View.VISIBLE)
            {
                mRatingHandler.removeCallbacks(mRatingRunnable);
                mRatingHandler.postDelayed(mRatingRunnable, 5000);
            }
            // Reactivate sensor orientation after delay

        }catch (Exception ex){ex.printStackTrace();}
    }

    private void reActivateOrientationDetection()
    {
        if(mRestoreOrientation!=null)
            mRestoreOrientation.cancel();
        mRestoreOrientation = new Timer();
        mRestoreOrientation.schedule(new TimerTask() {
            @Override
            public void run() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }, 10000);
    }

    private void playerTouch()
    {
        soundView.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        brightnessView.setProgress(8);

        mExoPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UiUtils.log("Touch","pointer "+motionEvent.getPointerCount()+","+motionEvent.getAction());
                //scaleGestureDetector.onTouchEvent(motionEvent);
                if(mExoPlayerIsLocked)
                {
                    exoUnLock.setVisibility(View.VISIBLE);
                    mLockHandler.removeCallbacks(mLockRunnable);
                    mLockHandler.postDelayed(mLockRunnable, 2000);
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    lastx = motionEvent.getX();
                    downy = motionEvent.getY();
                    endheight = downy - getResources().getDimensionPixelSize(R.dimen.dp_180);
                    diffheight = endheight - downy;
                    currentprogress = soundView.getProgress();
                    currentbrightprogress= brightnessView.getProgress();
                    first = true;
                    second = true;
                    selected = 0;

                    UiUtils.log("Touch", "width "+mExoPlayerView.getWidth() + "");

                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    UiUtils.log("Touch","myrb "+ "x=" + lastx + "y=" + downy + "");
                    UiUtils.log("Touch","myrb "+ "x=" + motionEvent.getX() + "y=" + motionEvent.getY() + "," + motionEvent.getAction());
                    float xdistance = motionEvent.getX() - lastx;
                    float ydistance = motionEvent.getY() - downy;
                    UiUtils.log("Touch","Value "+ "x=" + xdistance + "y=" + ydistance + "");
                    if (first && Math.abs(xdistance) == 0 && Math.abs(ydistance) == 0) {

                    } else if ((second && Math.abs(xdistance) < Math.abs(ydistance)) || selected == 1) {
                        if (selected == 0) {
                            selected = 1;
                            first = false;
                            second = true;
                            if(motionEvent.getX()<view.getWidth()/2.0f){
                                volumecontainerView.setVisibility(View.VISIBLE);
                                brightnesscontainerView.setVisibility(View.GONE);
                            }
                            else {
                                volumecontainerView.setVisibility(View.GONE);
                                brightnesscontainerView.setVisibility(View.VISIBLE);
                            }
                        }
                        UiUtils.log("Touch","endheight "+ endheight + " y= "+ motionEvent.getY());
                        float tempwidth = endheight - motionEvent.getY();
                        UiUtils.log("Touch","tempwidth "+ tempwidth);
                        UiUtils.log("Touch","sound "+ soundView.getMaxprogess() + " diffheight "+ diffheight);
                        float progress = (tempwidth * soundView.getMaxprogess()) / diffheight;
                        UiUtils.log("Touch","progress "+ (soundView.getMaxprogess() - progress) + "");
                        float tempnumber = (soundView.getMaxprogess() - progress);
                        int jprogress = (int)tempnumber /*(soundView.getMaxprogess() - progress)*/ ;
                        if(volumecontainerView.getVisibility()==View.VISIBLE){
                            int prog = (currentprogress + jprogress) ;
                            UiUtils.log("Touch","final progress "+ prog + "");
                            //int soundProg =
                            soundView.setProgress(prog);
                            if (prog > soundView.getMaxprogess())
                                soundView.setProgress(soundView.getMaxprogess());
                            else if (prog < 0) soundView.setProgress(0);
                            else {
                                soundView.setProgress(prog);
                            }

                        }
                        else {
                            int prog = currentbrightprogress + jprogress;
                            UiUtils.log("Touch","final progress "+ prog + "");
                            if (prog > brightnessView.getMaxprogess())
                                brightnessView.setProgress(brightnessView.getMaxprogess());
                            else if (prog < 0) brightnessView.setProgress(0);
                            else {
                                float brightness = prog/ 15.0f;
                                WindowManager.LayoutParams lp = getWindow().getAttributes();
                                lp.screenBrightness = brightness;
                                getWindow().setAttributes(lp);
                                UiUtils.log("Touch","final brightness "+ brightness + "");
                                brightnessView.setProgress(prog);
                                progress_brightness_text.setText(String.valueOf(prog));
                            }
                        }

                        UiUtils.log("Touch","scroll "+ "vertical");
                    } /*else if (third || selected == 2) {

                        if (selected == 0) {
                            if (player.isPlaying()) {
                                isdonebyus = true;
                                player.pause();
                            }
                            second = false;
                            first = false;
                            third = true;
                            selected = 2;
                            playpausebutton.setVisibility(View.GONE);
                            bottomview.setVisibility(View.VISIBLE);
                            //toolbar.setVisibility(View.GONE);
                            seeklay.setVisibility(View.VISIBLE);

                        }

                        int progress = (int) ((60000 * (motionEvent.getX() - trackx)) / touchview.getWidth());

                        if (lastprogress != progress) {
                            player.seekTo(currentseek + progress);
                            dragseek.setProgress(currentseek + progress);
                            seektime.setText(milltominute(dragseek.getProgress()));
                            seekdelay.setText("[" + milltominute(progress) + "]");
                            // Log.e("scroll","horizontal"+milltominute(dragseek.getProgress())+","+milltominute(progress));
                        }
                        lastprogress = progress;


                    }*/
                    lastx = motionEvent.getX();
                    downy = motionEvent.getY();
                } else if(motionEvent.getPointerCount()==1&&motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(isscalegesture){
                        isscalegesture=false;
                    }
                    else {
                        /*seeklay.setVisibility(View.GONE);
                        if (isdonebyus) player.play();
                        isdonebyus = false;
                        if(islock){
                            if(unlockbtn.getVisibility()==View.GONE){
                                unlockbtn.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        unlockbtn.setVisibility(View.GONE);
                                    }
                                },2000);
                            }
                        }
                        else {
                            if (motionEvent.getX() == putx && motionEvent.getY() == puty && System.currentTimeMillis() - lasttime <= 1000) {
                                speedView.setVisibility(View.GONE);
                                if (isshow) {
                                    hideSystemUI();
                                    bottomview.setVisibility(View.GONE);
                                    toolbar.setVisibility(View.GONE);
                                } else {
                                    showSystemUI();
                                    playpausebutton.setVisibility(View.VISIBLE);
                                    bottomview.setVisibility(View.VISIBLE);
                                    toolbar.setVisibility(View.VISIBLE);
                                    hidehandler.postDelayed(hiderunnable,4000);

                                }
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        hideSystemUI();
                                        volumecontainerView.setVisibility(View.INVISIBLE);
                                        brightcontainer.setVisibility(View.INVISIBLE);
                                        bottomview.setVisibility(View.GONE);
                                        toolbar.setVisibility(View.GONE);
                                        playpausebutton.setVisibility(View.VISIBLE);

                                    }
                                }, 300);
                            }
                        }*/
                    }



                }
                else if(isscalegesture){

                }


                return false;
            }
        });
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        UiUtils.log("value is",""+newVal);
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus){
        int width=img_thumbnail.getWidth();
        int height=img_thumbnail.getHeight();
        UiUtils.log(TAG, "Height: " + height + " Width: " + width);

        if(height<550) {
            description.setMaxLines(4);
        }
        else if(height>550 && height<700) {
            if(width>450) {
                descLength = 200;
                description.setMaxLines(6);
            }else
            {
                descLength = 400;
                description.setMaxLines(6);
            }
        }
        else if(height>=700) {
            if(width>500) {
                descLength = 600;
                description.setMaxLines(10);
            }else
            {
                descLength = 200;
                description.setMaxLines(6);
            }
        }

        if(video!=null) {
            String desc = video.getDescription();
            if (checkString(desc)) {
                //                // if (mMessageList.get(position).getChatType().equalsIgnoreCase("0")) {
                if (desc.length() > descLength) {
                    desc = desc.substring(0, descLength) + "    ";
                    //  messageText.setText((inputText) + Html.fromHtml("  <font color='#4A90E2'>...Read more</font>"));
                    String styledText = desc + "<font color='#4C97C4'>...Read more</font>";
                    description.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
                } else {
                    description.setText(Html.fromHtml(desc));
                }

                //   } else {
            }
        }

    }*/

    private void checkWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

        UiUtils.log(TAG, "Height: "+ dimension[0]);
        UiUtils.log(TAG, "Height dp: "+ dimension[2]);
        UiUtils.log(TAG, "Width: "+ width);
        UiUtils.log(TAG, "Width dp: "+ dp);

        //1080 411 s9
        /*Toast.makeText(VideoPageActivity.this,"Height: "+ dimension[0] + " \nHeight dp: "+ dimension[2] +
                "\nWidth: "+ dimension[1] + " \nWidth dp: "+ dimension[3],Toast.LENGTH_LONG).show();*/

        if(dp<400) {

            /*if (width > 1300)
                descLength = 200;*/
        }else if(dp>400 && dp<500)
        {
            if(width>1300) {
                descLength = 200;
            }else
            {
                descLength = 120;
                //description.setMaxLines(5);
            }

        }else if(dp>500 && dp<600)
        {
            UiUtils.log(TAG, "margin(500-600)");
            setTextSize(18,16,14);

            descLength = 200;
            description.setMaxLines(6);

        }else if(dp>600 && dp<700)
        {
            UiUtils.log(TAG, "margin(600-700)");
            setTextSize(18,16,14);
        }else if(dp>700 && dp<800)
        {
            UiUtils.log(TAG, "margin(700-800)");
            setTextSize(18,16,14);
        }else if(dp>800)
        {
            UiUtils.log(TAG, "margin(800)");
            setTextSize(20,18,16);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img_rating.getLayoutParams();
            params.width = 70;
            params.height = 70;
            params.setMargins(0, 40, 40, 0);
            img_rating.setLayoutParams(params);

            setTextSize(20,18,16);
        }
    }

    private void setTextSize(float headingSize, float mediumSize,float size)
    {
        btnTrailer.setTextSize(size);
        videoTitle.setTextSize(headingSize);
        descHeader.setTextSize(mediumSize);
        description.setTextSize(mediumSize);
        infoHeader.setTextSize(mediumSize);
        directorText.setTextSize(size);
        starringText.setTextSize(size);
        genreText.setTextSize(size);
        ratingText.setTextSize(size);
        tvWatchNext.setTextSize(mediumSize);
    }

    public void showFullDescription()
    {

        //Now we need an AlertDialog.Builder object
        Dialog dialog = new Dialog(VideoPageActivity.this);

        dialog.setContentView(R.layout.text_full_description);
        dialog.setCancelable(false);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); // change this to your dialog.

        //params.y = 10; // Here is the param to set your dialog position. Same with params.x
        //dialog.getWindow().setAttributes(params);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView txt_full_description = dialog.findViewById(R.id.txt_full_description);
        txt_full_description.setText(video.getDescription());

        dialog.findViewById(R.id.img_close).setOnClickListener(v-> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

}
