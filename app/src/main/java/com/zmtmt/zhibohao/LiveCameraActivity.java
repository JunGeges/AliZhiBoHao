package com.zmtmt.zhibohao;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.livecloud.event.AlivcEvent;
import com.alibaba.livecloud.event.AlivcEventResponse;
import com.alibaba.livecloud.event.AlivcEventSubscriber;
import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.alibaba.livecloud.live.AlivcMediaRecorder;
import com.alibaba.livecloud.live.AlivcMediaRecorderFactory;
import com.alibaba.livecloud.live.AlivcStatusCode;
import com.alibaba.livecloud.live.OnLiveRecordErrorListener;
import com.alibaba.livecloud.live.OnNetworkStatusListener;
import com.alibaba.livecloud.live.OnRecordStatusListener;
import com.alibaba.livecloud.model.AlivcWatermark;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.zmtmt.zhibohao.entity.Comment;
import com.zmtmt.zhibohao.entity.CommentContent;
import com.zmtmt.zhibohao.entity.Products;
import com.zmtmt.zhibohao.entity.ShareInfo;
import com.zmtmt.zhibohao.tools.CommentAdapter;
import com.zmtmt.zhibohao.tools.HttpUtils;
import com.zmtmt.zhibohao.tools.ProductsAdapter;
import com.zmtmt.zhibohao.tools.ShareUtils;
import com.zmtmt.zhibohao.tools.Utils;
import com.zmtmt.zhibohao.widget.CountTimeView;
import com.zmtmt.zhibohao.widget.CustomPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zmtmt.zhibohao.R.id.ll_camera;

public class LiveCameraActivity extends Activity implements View.OnClickListener, View.OnTouchListener, CustomPopupWindow.ViewInterface {

    private PopupWindow mPop_settings;
    private LinearLayout mLl_share;
    private PopupWindow mSharePopupWindow;
    private int mCurrFacing;
    private int mScaledTouchSlop;
    private CommentAdapter commentAdapter;

    public static class RequestBuilder {
        String rtmpUrl;
        int videoResolution;
        boolean isPortrait;
        int cameraFacing;
        String watermarkUrl;
        int dx;
        int dy;
        int site;
        int bestBitrate;
        int minBitrate;
        int maxBitrate;
        int initBitrate;
        int frameRate;
        ArrayList<Products> pList;
        String eventUrl;//请求地址
        int memberlevelId;
        ShareInfo mShareInfo; //分享信息带直播会话ID
        String openID;

        public RequestBuilder rtmpUrl(String url) {
            this.rtmpUrl = url;
            return this;
        }

        public RequestBuilder videoResolution(int resolution) {
            this.videoResolution = resolution;
            return this;
        }

        public RequestBuilder portrait(boolean isPortrait) {
            this.isPortrait = isPortrait;
            return this;
        }

        public RequestBuilder cameraFacing(int cameraFacing) {
            this.cameraFacing = cameraFacing;
            return this;
        }

        public RequestBuilder watermarkUrl(String url) {
            this.watermarkUrl = url;
            return this;
        }

        public RequestBuilder dx(int dx) {
            this.dx = dx;
            return this;
        }

        public RequestBuilder dy(int dy) {
            this.dy = dy;
            return this;
        }

        public RequestBuilder site(int site) {
            this.site = site;
            return this;
        }

        public RequestBuilder bestBitrate(int bestBitrate) {
            this.bestBitrate = bestBitrate;
            return this;
        }

        public RequestBuilder minBitrate(int minBitrate) {
            this.minBitrate = minBitrate;
            return this;
        }

        public RequestBuilder maxBitrate(int maxBitrate) {
            this.maxBitrate = maxBitrate;
            return this;
        }

        public RequestBuilder initBitrate(int initBitrate) {
            this.initBitrate = initBitrate;
            return this;
        }

        public RequestBuilder frameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public RequestBuilder productList(ArrayList<Products> list) {
            this.pList = list;
            return this;
        }

        public RequestBuilder eventUrl(String url) {
            this.eventUrl = url;
            return this;
        }

        public RequestBuilder memberlevelId(int memberlevelId) {
            this.memberlevelId = memberlevelId;
            return this;
        }

        public RequestBuilder shareInfo(ShareInfo shareInfo) {
            this.mShareInfo = shareInfo;
            return this;
        }

        public RequestBuilder openId(String openID) {
            this.openID = openID;
            return this;
        }

        public Intent build(Context context) {
            Intent intent = new Intent(context, LiveCameraActivity.class);
            intent.putExtra(URL, rtmpUrl);
            intent.putExtra(VIDEO_RESOLUTION, videoResolution);
            intent.putExtra(SCREENORIENTATION, isPortrait);
            intent.putExtra(FRONT_CAMERA_FACING, cameraFacing);
            intent.putExtra(WATERMARK_PATH, watermarkUrl);
            intent.putExtra(WATERMARK_DX, dx);
            intent.putExtra(WATERMARK_DY, dy);
            intent.putExtra(WATERMARK_SITE, site);
            intent.putExtra(BEST_BITRATE, bestBitrate);
            intent.putExtra(MIN_BITRATE, minBitrate);
            intent.putExtra(MAX_BITRATE, maxBitrate);
            intent.putExtra(INIT_BITRATE, initBitrate);
            intent.putExtra(FRAME_RATE, frameRate);
            intent.putExtra(SHARE_INFO, mShareInfo);
            intent.putExtra(MEMBERLEVEL_ID, memberlevelId);
            intent.putExtra(EVENT_URL, eventUrl);
            intent.putExtra(PRODUCT_LIST, pList);
            intent.putExtra(OPEN_ID, openID);
            return intent;
        }

    }

    private static final String TAG = "LiveCameraActivity";
    public final static String URL = "url";
    public final static String VIDEO_RESOLUTION = "video_resolution";
    public final static String SCREENORIENTATION = "screen_orientation";
    public final static String FRONT_CAMERA_FACING = "front_camera_face";

    public final static String WATERMARK_PATH = "watermark_path";
    public final static String WATERMARK_DX = "watermark_dx";
    public final static String WATERMARK_DY = "watermark_dy";
    public final static String WATERMARK_SITE = "watermark_site";
    public final static String BEST_BITRATE = "best-bitrate";
    public final static String MIN_BITRATE = "min-bitrate";
    public final static String MAX_BITRATE = "max-bitrate";
    public final static String INIT_BITRATE = "init-bitrate";
    public final static String FRAME_RATE = "frame-rate";
    public final static String SHARE_INFO = "share_info";
    public final static String MEMBERLEVEL_ID = "memberlevel_id";
    public final static String EVENT_URL = "event_url";
    public final static String PRODUCT_LIST = "product_list";
    public final static String OPEN_ID = "open_id";

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private final int PERMISSION_DELAY = 100;
    private boolean mHasPermission = false;
    private AlertDialog illegalArgumentDialog = null;
    private CountTimeView mCountTimeView;
    private SurfaceView _CameraSurface;
    private AlivcMediaRecorder mMediaRecorder;
    private Surface mPreviewSurface;
    private Map<String, Object> mConfigure = new HashMap<>();
    private boolean isRecording = false;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private PopupWindow pop_products;
    private ListView pop_products_listview;
    private View c_pop_talk_empty_tip;
    private ListView mListView;
    private PopupWindow pop_comment;
    private Timer timer_comment = null;

    private ImageView mRecorderButton;//录制按钮
    private ImageView btn_pop_talk;
    private ImageView btn_pop_pro;
    private ArrayList<Products> pList;
    private String eventUrl;//请求地址
    private int memberlevelId;
    private ShareInfo mShareInfo; //分享信息带直播会话ID
    //评论
    private static final int UI_EVENT_GET_COMMENT = 11;
    private String pushUrl;
    private int resolution;
    private boolean screenOrientation;
    private int cameraFrontFacing;
    private AlivcWatermark mWatermark;
    private int bestBitrate;
    private int minBitrate;
    private int maxBitrate;
    private int initBitrate;
    private int frameRate;
    private TextView mTv_watch_person;
    private ArrayList<Comment> cList = new ArrayList<Comment>();
    private int firstSize = 0;
    private int lastSize = 0;
    private static final String IS_NEW = "1";//最新数据
    private String ID = "0"; //默认是0  最大评论值的ID  每次都替换 获取最新数据
    private String person = "0";//在线人数
    private Timer timer_time = null;
    //推流时间
    private static final int UI_EVENT_RECORD_TIME = 12;
    private static final int UI_EVENT_START_RECORD = 6;
    private static final String OPERATION = "state";
    private static final String START_STATE = "2";
    private String openID;//直播用户openid
    private static final String STOP_STATE = "1";//暂停直播状态码
    private String currentTime = null;
    private boolean isFirstPush = true;
    private static final int UI_EVENT_RECORDER_STOPPED = 2;//录制停止
    /**
     * 是否已经记次
     **/
    private boolean isCount = false;
    private CircleImageView wx_user_icon;
    private TextView wx_user_name;
    private LinearLayout mLl_camera;
    private LinearLayout mLl_sound;
    private LinearLayout mLl_beauty;
    private LinearLayout mLl_flash;
    private TextView tv_sound, tv_beauty, tv_flash;
    private ImageView iv_sound, iv_beauty, iv_flash;
    private boolean flash_is_open = false;
    private boolean sound_is_open = true;
    private boolean beauty_is_open = false;
    private boolean push_is_open = false;
    private ImageView settings;
    private LinearLayout push_state_ll;
    private boolean push_state = true;
    private int slideDownX;
    private int slideUpX;
    private RelativeLayout mRelativeLayout;
    private Bitmap mBitmap;

    public static void startActivity(Context context,
                                     RequestBuilder builder) {
        Intent intent = builder.build(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_camera);
        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        } else {
            mHasPermission = true;
        }
        mScaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();//判断是否滑动的基准
        getExtraData();
        initView();
        setRequestedOrientation(screenOrientation ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //采集
        _CameraSurface = (SurfaceView) findViewById(R.id.camera_surface);
        _CameraSurface.getHolder().addCallback(_CameraSurfaceCallback);
        _CameraSurface.setOnTouchListener(mOnTouchListener);
        //对焦，缩放
        mDetector = new GestureDetector(_CameraSurface.getContext(), mGestureDetector);
        mScaleDetector = new ScaleGestureDetector(_CameraSurface.getContext(), mScaleGestureListener);

        mMediaRecorder = AlivcMediaRecorderFactory.createMediaRecorder();
        mMediaRecorder.init(this);

        //注册推流状态回调监听
        mMediaRecorder.setOnRecordStatusListener(mRecordStatusListener);
        mMediaRecorder.setOnNetworkStatusListener(mOnNetworkStatusListener);
        mMediaRecorder.setOnRecordErrorListener(mOnErrorListener);

        mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, cameraFrontFacing);
        mConfigure.put(AlivcMediaFormat.KEY_I_FRAME_INTERNAL, 3);
        mConfigure.put(AlivcMediaFormat.KEY_MAX_ZOOM_LEVEL, 3);
        mConfigure.put(AlivcMediaFormat.KEY_OUTPUT_RESOLUTION, resolution);
        mConfigure.put(AlivcMediaFormat.KEY_MAX_VIDEO_BITRATE, maxBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_BEST_VIDEO_BITRATE, bestBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_MIN_VIDEO_BITRATE, minBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_INITIAL_VIDEO_BITRATE, initBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_DISPLAY_ROTATION, screenOrientation ? AlivcMediaFormat.DISPLAY_ROTATION_90 : AlivcMediaFormat.DISPLAY_ROTATION_0);
//        mConfigure.put(AlivcMediaFormat.KEY_EXPOSURE_COMPENSATION, 10);//曝光度
        mConfigure.put(AlivcMediaFormat.KEY_WATERMARK, mWatermark);
        mConfigure.put(AlivcMediaFormat.KEY_FRAME_RATE, frameRate);
        mConfigure.put(AlivcMediaFormat.KEY_AUDIO_BITRATE, 32000);
        mConfigure.put(AlivcMediaFormat.KEY_AUDIO_SAMPLE_RATE, 44100);

        setLogo();

        wx_user_name.post(new Runnable() {
            @Override
            public void run() {
                View pop_talk_view = LayoutInflater.from(LiveCameraActivity.this).inflate(R.layout.pop_talk_layout, null);
                c_pop_talk_empty_tip = pop_talk_view.findViewById(R.id.c_pop_talk_empty_tip);
                pop_comment = new CustomPopupWindow.Builder(LiveCameraActivity.this)
                        .setView(pop_talk_view)
                        .setWidthAndHeight(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
                        .setOutsideTouchable(true)
                        .setAnimationStyle(R.style.AnimationFade)
                        .create();
                pop_comment.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        int visible = push_state ? View.VISIBLE : View.INVISIBLE;
                        push_state_ll.setVisibility(visible);
                    }
                });
                mListView = (ListView) pop_talk_view.findViewById(R.id.lv_pop_comment);
            }
        });
    }

    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        push_state_ll = (LinearLayout) findViewById(R.id.push_state_ll);
        settings = (ImageView) findViewById(R.id.iv_settings);
        settings.setOnClickListener(this);
        wx_user_name = (TextView) findViewById(R.id.wx_user_name_tv);
        wx_user_icon = (CircleImageView) findViewById(R.id.wx_user_icon_iv);
        mCountTimeView = (CountTimeView) findViewById(R.id.count_time_view);
        mRecorderButton = (ImageView) findViewById(R.id.iv_push);
        mRecorderButton.setOnClickListener(this);
        btn_pop_pro = (ImageView) findViewById(R.id.btn_pro);
        btn_pop_pro.setOnClickListener(this);
        btn_pop_talk = (ImageView) findViewById(R.id.btn_talk);
        btn_pop_talk.setOnClickListener(this);
        mTv_watch_person = (TextView) findViewById(R.id.tv_watch_person);
        mRelativeLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                slideDownX = (int) event.getX();
                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
                slideUpX = (int) event.getX();
                if (Math.abs(slideUpX - slideDownX) > mScaledTouchSlop && push_state) {
                    push_state_ll.setVisibility(View.INVISIBLE);
                    push_state = false;
                } else {
                    push_state_ll.setVisibility(View.VISIBLE);
                    push_state = true;
                }
                break;
        }
        return true;
    }

    /**
     * 轮询获取评论
     */
    public void getComment() {
        timer_comment = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = mUIEventHandler.obtainMessage();
                message.what = UI_EVENT_GET_COMMENT;
                mUIEventHandler.sendMessage(message);
            }
        };
        timer_comment.schedule(task, 1000, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_settings:
                showSettingPop();
                break;

            case R.id.iv_push:
                setPushStream();
                break;

            case R.id.ll_camera:
                //相机
                setCamera();
                break;

            case R.id.ll_sound:
                //声音
                setSound();
                break;

            case R.id.ll_beauty:
                //美颜
                setBeauty();
                break;

            case R.id.ll_flash:
                //闪光灯
                setFlash();
                break;

            case R.id.ll_share:
                //分享操作
                showSharePop();
                break;

            case R.id.btn_pro:
                //商品推荐处理
                showPopPro();
                break;

            case R.id.btn_talk:
                //评论处理
                showPopTalk();
                break;

            case R.id.c_ll_share_wx:
                ShareUtils.shareToWX(mShareInfo, 2, mBitmap);
                if (mPop_settings != null) mPop_settings.dismiss();
                if (mSharePopupWindow != null) mSharePopupWindow.dismiss();
                break;

            case R.id.c_ll_share_pyq:
                ShareUtils.shareToWX(mShareInfo, 1,mBitmap);
                if (mPop_settings != null) mPop_settings.dismiss();
                if (mSharePopupWindow != null) mSharePopupWindow.dismiss();
                break;
        }
    }

    private void setPushStream() {
        if (!push_is_open) {
            Message msg = Message.obtain();
            msg.what = UI_EVENT_START_RECORD;
            mUIEventHandler.sendMessage(msg);
            push_is_open = true;
        } else {
            Message msg = Message.obtain();
            msg.what = UI_EVENT_RECORDER_STOPPED;
            mUIEventHandler.sendMessage(msg);
            push_is_open = false;
        }
    }

    private void setFlash() {
        if (!flash_is_open) {
            if (AlivcMediaFormat.CAMERA_FACING_FRONT == mCurrFacing) {
                Utils.showToast(this, "前置摄像头不支持闪关灯");
                return;
            }
            iv_flash.setImageResource(R.drawable.button_light_on);
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_FLASH_MODE_ON);
            tv_flash.setText("关闭闪光");
            flash_is_open = true;
            Utils.showToast(this, "闪光灯已开启");
        } else {
            flash_is_open = false;
            iv_flash.setImageResource(R.drawable.button_light_off);
            mMediaRecorder.removeFlag(AlivcMediaFormat.FLAG_FLASH_MODE_ON);
            tv_flash.setText("开启闪光");
            Utils.showToast(this, "闪光灯已关闭");
        }
    }

    private void setBeauty() {
        if (!beauty_is_open) {
            iv_beauty.setImageResource(R.drawable.beauty);
            tv_beauty.setText("关闭美颜");
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
            beauty_is_open = true;
            Utils.showToast(this, "美颜已开启");
        } else {
            iv_beauty.setImageResource(R.drawable.no_beauty);
            tv_beauty.setText("开启美颜");
            mMediaRecorder.removeFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
            beauty_is_open = false;
            Utils.showToast(this, "美颜已关闭");
        }
    }

    private void setSound() {
        if (!sound_is_open) {
            mMediaRecorder.removeFlag(AlivcMediaFormat.FLAG_MUTE_ON);
            iv_sound.setImageResource(R.drawable.button_volume_on);
            tv_sound.setText("静音        ");
            Utils.showToast(this, "声音已开启");
            sound_is_open = true;
        } else {
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_MUTE_ON);
            iv_sound.setImageResource(R.drawable.button_volume_off);
            tv_sound.setText("开启声音");
            Utils.showToast(this, "声音已关闭");
            sound_is_open = false;
        }
    }

    private void setCamera() {
        mCurrFacing = mMediaRecorder.switchCamera();
        if (mCurrFacing == AlivcMediaFormat.CAMERA_FACING_FRONT) {
            if (flash_is_open) {
                iv_flash.setImageResource(R.drawable.button_light_off);
                mMediaRecorder.removeFlag(AlivcMediaFormat.FLAG_FLASH_MODE_ON);
                tv_flash.setText("开启闪光");
                Utils.showToast(this, "闪光灯已关闭");
                flash_is_open = true;
            }
        }
        mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, mCurrFacing);
    }

    private void getExtraData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pushUrl = bundle.getString(URL);
            resolution = bundle.getInt(VIDEO_RESOLUTION);
            screenOrientation = bundle.getBoolean(SCREENORIENTATION);
            cameraFrontFacing = bundle.getInt(FRONT_CAMERA_FACING);
            mWatermark = new AlivcWatermark.Builder()
                    .watermarkUrl(bundle.getString(WATERMARK_PATH))
                    .paddingX(bundle.getInt(WATERMARK_DX))
                    .paddingY(bundle.getInt(WATERMARK_DY))
                    .site(bundle.getInt(WATERMARK_SITE))
                    .build();
            minBitrate = bundle.getInt(MIN_BITRATE);
            maxBitrate = bundle.getInt(MAX_BITRATE);
            bestBitrate = bundle.getInt(BEST_BITRATE);
            initBitrate = bundle.getInt(INIT_BITRATE);
            frameRate = bundle.getInt(FRAME_RATE);
            pList = bundle.getParcelableArrayList(PRODUCT_LIST);
            eventUrl = bundle.getString(EVENT_URL);
            memberlevelId = bundle.getInt(MEMBERLEVEL_ID);
            mShareInfo = bundle.getParcelable(SHARE_INFO);
            openID = bundle.getString(OPEN_ID);
            //获取分享缩略图
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBitmap = HttpUtils.getBitmapByUrl(mShareInfo.getImgUrl());
                }
            }).start();
        }
    }

    private void permissionCheck() {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissionManifest) {
            if (PermissionChecker.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionCheck = PackageManager.PERMISSION_DENIED;
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
        } else {
            mHasPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean hasPermission = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        int toastTip = 0;
                        if (Manifest.permission.CAMERA.equals(permissions[i])) {
                            toastTip = R.string.no_camera_permission;
                        } else if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                            toastTip = R.string.no_record_audio_permission;
                        }
                        if (toastTip != 0) {
                            Utils.showToast(this, toastTip + "");
                            hasPermission = false;
                        }
                    }
                }
                mHasPermission = hasPermission;
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreviewSurface != null) {
            mMediaRecorder.prepare(mConfigure, mPreviewSurface);
            Log.d("AlivcMediaRecorder", " onResume==== isRecording =" + isRecording + "=====");
        }
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_BITRATE_DOWN, mBitrateDownRes));//注册码率下降调整事件
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_BITRATE_RAISE, mBitrateUpRes));//注册码率上升调整事件
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC, mAudioCaptureSuccRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_DATA_DISCARD, mDataDiscardRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_INIT_DONE, mInitDoneRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC, mVideoEncoderSuccRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED, mVideoEncoderFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED, mVideoEncodeFrameFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED, mAudioEncodeFrameFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED, mAudioCaptureOpenFailedRes));
        addView(new TextView(LiveCameraActivity.this), "请点击右侧录制按钮开始(结束)直播");
    }

    @Override
    protected void onPause() {
        if (isRecording) {
            mMediaRecorder.stopRecord();
        }
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_BITRATE_DOWN);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_BITRATE_RAISE);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_DATA_DISCARD);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_INIT_DONE);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED);
        /**
         * 如果要调用stopRecord和reset()方法，则stopRecord（）必须在reset之前调用，否则将会抛出IllegalStateException
         */
        mMediaRecorder.reset();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        RecordLoggerManager.closeLoggerFile();
        mMediaRecorder.release();
        if (timer_comment != null) timer_comment.cancel();
        if (timer_time != null) timer_time.cancel();
        Map<String, String> params = new HashMap<String, String>();
        params.put("op", OPERATION);
        params.put("liveid", mShareInfo.getLiveId());
        params.put("state", STOP_STATE);
        params.put("openid", openID);
        params.put("livetime", String.valueOf(mCountTimeView.getTotalTime()));
        params.put("starttime", currentTime);
        requestChangeLiveState(params);
    }

    private Handler mUIEventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_EVENT_START_RECORD:
                    isRecording = true;
                    mMediaRecorder.startRecord(pushUrl);
                    try {
                        if (isFirstPush) {
                            currentTime = String.valueOf(System.currentTimeMillis() / 1000);
                            Logger.t(TAG).d("currentTime" + currentTime);
                            isFirstPush = false;
                        }
                        mRecorderButton.setBackgroundResource(R.drawable.ic_video_stop);
                        getComment();//轮询获取评论信息
                        checkRecordTime();//开始直播开始计次与号外棒扣取
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("op", OPERATION);
                        param.put("liveid", mShareInfo.getLiveId());
                        param.put("state", START_STATE);
                        param.put("openid", openID);
                        requestChangeLiveState(param);
                        mCountTimeView.setVisibility(View.VISIBLE);
                        mCountTimeView.startCountTime();
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_pushStream_start));
                    } catch (Exception e) {

                    }
                    break;

                case UI_EVENT_RECORDER_STOPPED:
                    mCountTimeView.stopCountTime();
                    mMediaRecorder.stopRecord();
                    isRecording = false;
                    mRecorderButton.setBackgroundResource(R.drawable.ic_video_start);
                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("op", OPERATION);
                    params.put("liveid", mShareInfo.getLiveId());
                    params.put("state", STOP_STATE);
                    params.put("openid", openID);
                    params.put("livetime", String.valueOf(mCountTimeView.getTotalTime()));
                    params.put("starttime", currentTime);
                    requestChangeLiveState(params);
                    int arg = msg.arg1;
                    if (arg == 13) {
                        //普通会员正常到达时间
                        timer_time.cancel();//取消获取时间的定时器
                        mRecorderButton.setEnabled(false);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_member_pushStreamTime));
                    } else if (arg == 14) {
                        //号外棒不足
                        timer_time.cancel();
                        mRecorderButton.setEnabled(false);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_member_shortage));
                    } else if (arg == 15) {
                        //会员过期
                        timer_time.cancel();
                        mRecorderButton.setEnabled(false);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_member_overdue));
                    } else if (arg == 16) {
                        //记次失败
                        timer_time.cancel();
                        mRecorderButton.setEnabled(false);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_member_count_error));
                    } else if (arg == 17) {
                        //超级会员正常到达的时间
                        timer_time.cancel();
                        mRecorderButton.setEnabled(false);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_member_pushStreamTime));
                    } else {
                        //正常的用户操作
                        timer_time.cancel();
                        mRecorderButton.setEnabled(true);
                        addView(new TextView(LiveCameraActivity.this), getString(R.string.c_pushStream_stop));
                    }
                    break;

                case UI_EVENT_RECORD_TIME:
                    postToServer();
                    break;

                case UI_EVENT_GET_COMMENT:
                    new CommentAsyncTask().execute(eventUrl);
                    break;
            }
        }
    };

    private void requestChangeLiveState(final Map<String, String> param) {
        new Thread() {
            @Override
            public void run() {
                HttpUtils.post(eventUrl + "applogin", param, new HttpUtils.NetWorkStatus() {
                    @Override
                    public void onSuccessful(String response) {

                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });//做网络请求 通知服务器 改变直播状态
            }
        }.start();
    }

    class CommentAsyncTask extends AsyncTask<String, Void, ArrayList<Comment>> {

        @Override
        protected ArrayList<Comment> doInBackground(final String... strings) {
            firstSize = cList.size();
            Map<String, String> params = new HashMap<String, String>();
            params.put("isNew", IS_NEW);
            params.put("liveid", mShareInfo.getLiveId());
            params.put("id", ID);
            HttpUtils.post(strings[0] + "roomlivegetajax", params, new HttpUtils.NetWorkStatus() {
                @Override
                public void onSuccessful(String response) {
                    if (response != null) {
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONArray array_1 = array.getJSONArray(0);
                            String isNew = array_1.getString(0);
                            ID = array_1.getString(1);
                            person = array_1.getString(2);
                            Log.i(TAG, "onSuccessful: " + person);
                            for (int i = 1; i < array.length(); i++) {
                                final Comment c = new Comment();//评论类
                                CommentContent c_content = new CommentContent();//评论内容类
                                JSONObject object = array.getJSONObject(i);
                                //commenttype  1：普通评论 2:主播推荐商品  3：系统礼物提示  4：系统商品买卖提示
                                String comment_type = object.getString("commenttype");
                                if (comment_type.equals("1")) {
                                    c_content.setCommentContent(object.getString("commentcontent"));
                                    c.setComment_content(c_content);
                                    c.setCommenttype(object.getString("commenttype"));
                                    c.setIssystem(object.getString("issystem"));

                                    c.setComment_head_url(object.getString("userimg"));
                                    c.setComment_nick_name(object.getString("usernickname"));
                                    c.setComment_floor(object.getString("louhao"));
                                    c.setComment_time(object.getString("addtime"));
                                    cList.add(c);

                                } else if (comment_type.equals("2")) {//商品推荐
                                    String comment_json = object.getString("commentcontent");
                                    JSONObject object_comment_recommend_products = new JSONObject(comment_json);
                                    c_content.setName(object_comment_recommend_products.getString("name"));
                                    c.setComment_content(c_content);
                                    c.setCommenttype(comment_type);
                                    c.setIssystem(object.getString("issystem"));

                                    c.setComment_head_url(object.getString("userimg"));
                                    c.setComment_nick_name(object.getString("usernickname"));
                                    c.setComment_floor(object.getString("louhao"));
                                    c.setComment_time(object.getString("addtime"));
                                    cList.add(c);
                                } else if (comment_type.equals("3")) {//送礼物的
                                    String comment_json = object.getString("commentcontent");
                                    JSONObject object_comment = new JSONObject(comment_json);
                                    c_content.setName(object_comment.getString("name").substring(4));//设置礼物名字
                                    c.setComment_content(c_content);
                                    c.setCommenttype(comment_type);
                                    c.setIssystem(object.getString("issystem"));

                                    c.setComment_head_url(object.getString("userimg"));
                                    c.setComment_nick_name(object.getString("usernickname"));
                                    c.setComment_floor(object.getString("louhao"));
                                    c.setComment_time(object.getString("addtime"));
                                    cList.add(c);

                                } else if (comment_type.equals("4")) {//购买
                                    String comment_json = object.getString("commentcontent");
                                    JSONObject object_comment_products = new JSONObject(comment_json);
                                    c_content.setName(object_comment_products.getString("name"));//设置购买商品的名字
                                    c.setComment_content(c_content);
                                    c.setCommenttype(comment_type);
                                    c.setIssystem(object.getString("issystem"));

                                    c.setComment_head_url(object.getString("userimg"));
                                    c.setComment_nick_name(object.getString("usernickname"));
                                    c.setComment_floor(object.getString("louhao"));
                                    c.setComment_time(object.getString("addtime"));
                                    cList.add(c);
                                } else {
                                    //这个评论不添加到集合里面去

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            lastSize = cList.size();
                        }
                    }
                }

                @Override
                public void onFailed(String error) {
                    Log.i(TAG, "onFailed: " + error);
                }
            });
            return cList;
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> comments) {
            c_pop_talk_empty_tip.setVisibility(comments.size() > 0 ? View.GONE : View.VISIBLE);
            //更新在线人数
/*            int personFormat = Integer.parseInt(person);
            Log.i(TAG, "onPostExecute: " + person);
            if (personFormat >= 10000) {
                int num = personFormat / 10000;
                int num2 = personFormat / 1000 % 10;
                int num3 = personFormat / 100 % 10;
                person = num + "." + num2 + num3 + "万";
            }*/
                mTv_watch_person.setText(person);
            if (commentAdapter == null) {
                commentAdapter = new CommentAdapter(LiveCameraActivity.this, comments);
                mListView.setAdapter(commentAdapter);
            } else {
                if (firstSize != lastSize) {
                    commentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setLogo() {
        //微信登录后存的用户信息
        SharedPreferences sp = getSharedPreferences("WXUserParams", MODE_PRIVATE);
        final String wxLogoUrl = sp.getString("WXLogoUrl", null);
        final String wxNickName = sp.getString("WXNickName", null);
        Glide.with(this).load(wxLogoUrl).into(wx_user_icon);
        wx_user_name.setText(wxNickName);
    }

    /**
     * 开始直播每分钟检查会员扣取号外棒以及直播记次情况
     */
    private void checkRecordTime() {
        timer_time = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = UI_EVENT_RECORD_TIME;
                mUIEventHandler.sendMessage(msg);
            }
        };
        timer_time.schedule(timerTask, 60000, 60000);
    }

    /**
     * 请求后台扣取号外棒以及记次，根据响应参数做出处理
     */
    private void postToServer() {
        int recordTime = (int) mCountTimeView.getTotalTime();
        if (!isCount && (recordTime >= 300)) {
            // 记次失后的五次重新记次如果没成功，就停止推流
            if (recordTime > 600) {//memberlevelId<3
                //如果到10分钟还没记次成功就强制停止推流
                Message msg = Message.obtain();
                msg.arg1 = 16;
                msg.what = UI_EVENT_RECORDER_STOPPED;
                mUIEventHandler.sendMessage(msg);
            }
            final Map<String, String> params = new HashMap<String, String>();
            params.put("openid", openID);
            params.put("liveid", mShareInfo.getLiveId());
            RequestTask task = new RequestTask(params);
            task.execute(eventUrl + "liveconsumeajax");
        }
        if (recordTime >= 2700 && (memberlevelId == 0 || memberlevelId == 1 || memberlevelId == 2)) {
            Logger.t(TAG).d("普通会员" + memberlevelId);
            Message msg = Message.obtain();
            msg.arg1 = 13;
            msg.what = UI_EVENT_RECORDER_STOPPED;
            mUIEventHandler.sendMessage(msg);
        } else {
            if (recordTime >= 14400 && (memberlevelId > 2)) {//4  不限时3 5
                Logger.t(TAG).d("超级会员" + memberlevelId);
                Message msg = Message.obtain();
                msg.arg1 = 17;
                msg.what = UI_EVENT_RECORDER_STOPPED;
                mUIEventHandler.sendMessage(msg);
            }
        }
    }

    private int stateCode;

    class RequestTask extends AsyncTask<String, Void, Integer> {
        public Map<String, String> requestParams;


        public RequestTask(Map<String, String> params) {
            this.requestParams = params;
        }

        @Override
        protected Integer doInBackground(String... params) {
            HttpUtils.post(params[0], requestParams, new HttpUtils.NetWorkStatus() {
                @Override
                public void onSuccessful(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        stateCode = object.getInt("response");
                        Logger.t(TAG).d("stateCode" + stateCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String error) {

                }
            });
            return stateCode;
        }

        @Override
        protected void onPostExecute(Integer stateCode) {
            super.onPostExecute(stateCode);
            switch (stateCode) {
                //记次成功
                case 200:
                    isCount = true;
                    Logger.t(TAG).d(getString(R.string.count_ok));
                    break;

                //号外棒不足
                case 300:
                    Logger.t(TAG).d(getString(R.string.insufficient));
                    Message msg_300 = Message.obtain();
                    msg_300.arg1 = 14;
                    msg_300.what = UI_EVENT_RECORDER_STOPPED;
                    mUIEventHandler.sendMessage(msg_300);
                    break;

                //会员过期
                case 301:
                    Logger.t(TAG).d(getString(R.string.members_expired));
                    Message msg_301 = Message.obtain();
                    msg_301.arg1 = 15;
                    msg_301.what = UI_EVENT_RECORDER_STOPPED;
                    mUIEventHandler.sendMessage(msg_301);
                    break;

                //非法操作
                case 400:
                    Logger.t(TAG).d(getString(R.string.illegal_operation));
                    break;

                //记次失败
                case 500:
                    Logger.t(TAG).d(getString(R.string.count_error));
                    isCount = false;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isRecording) {
            Utils.showToast(this, getString(R.string.c_backPressed));
        } else {
            if (mCountTimeView.getTotalTime() > 0) {
                Intent in = new Intent(this, endActivity.class);
                in.putExtra("livetime", mCountTimeView.getTotalTime());
                in.putExtra("livepersons", mTv_watch_person.getText().toString().trim());
                in.putExtra("shareinfo", mShareInfo);
                startActivity(in);
                finish();
            } else {
                finish();
            }

        }
    }

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector.OnGestureListener mGestureDetector = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (mPreviewWidth > 0 && mPreviewHeight > 0) {
                float x = motionEvent.getX() / mPreviewWidth;
                float y = motionEvent.getY() / mPreviewHeight;
                mMediaRecorder.focusing(x, y);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDetector.onTouchEvent(motionEvent);
            mScaleDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mMediaRecorder.setZoom(scaleGestureDetector.getScaleFactor());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }
    };

    private void startPreview(final SurfaceHolder holder) {
        if (!mHasPermission) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPreview(holder);
                }
            }, PERMISSION_DELAY);
            return;
        }
        mMediaRecorder.prepare(mConfigure, mPreviewSurface);
        mMediaRecorder.setPreviewSize(_CameraSurface.getMeasuredWidth(), _CameraSurface.getMeasuredHeight());
        if ((int) mConfigure.get(AlivcMediaFormat.KEY_CAMERA_FACING) == AlivcMediaFormat.CAMERA_FACING_FRONT) {
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
        }
    }

    private final SurfaceHolder.Callback _CameraSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setKeepScreenOn(true);
            mPreviewSurface = holder.getSurface();
            startPreview(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mMediaRecorder.setPreviewSize(width, height);
            mPreviewWidth = width;
            mPreviewHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mPreviewSurface = null;
            mMediaRecorder.stopRecord();
            mMediaRecorder.reset();
        }
    };

    private OnRecordStatusListener mRecordStatusListener = new OnRecordStatusListener() {
        @Override
        public void onDeviceAttach() {
        }

        @Override
        public void onDeviceAttachFailed(int i) {

        }


        @Override
        public void onSessionAttach() {
            if (isRecording && !TextUtils.isEmpty(pushUrl)) {
                mMediaRecorder.startRecord(pushUrl);
            }
            mMediaRecorder.focusing(0.5f, 0.5f);
        }

        @Override
        public void onSessionDetach() {

        }

        @Override
        public void onDeviceDetach() {

        }

        @Override
        public void onIllegalOutputResolution() {
            Utils.showToast(LiveCameraActivity.this, R.string.illegal_output_resolution + "");
        }
    };

    private OnNetworkStatusListener mOnNetworkStatusListener = new OnNetworkStatusListener() {
        @Override
        public void onNetworkBusy() {
            addView(new TextView(LiveCameraActivity.this), "当前网络波动，会影响直播流畅度");
        }

        @Override
        public void onNetworkFree() {
            addView(new TextView(LiveCameraActivity.this), "当前网络良好");
        }

        @Override
        public void onConnectionStatusChange(int status) {

            switch (status) {
                case AlivcStatusCode.STATUS_CONNECTION_START:
                    addView(new TextView(LiveCameraActivity.this), "正在连接服务器...");
                    break;
                case AlivcStatusCode.STATUS_CONNECTION_ESTABLISHED:
                    addView(new TextView(LiveCameraActivity.this), "与服务器连接建立成功");
                    break;
                case AlivcStatusCode.STATUS_CONNECTION_CLOSED:
                    addView(new TextView(LiveCameraActivity.this), "与服务器断开连接");
                    break;
            }
        }

        @Override
        public boolean onNetworkReconnectFailed() {
            addView(new TextView(LiveCameraActivity.this), "长时间重连失败，已不适合直播，请退出");
            mMediaRecorder.stopRecord();
            showIllegalArgumentDialog("网络重连失败");
            return false;
        }
    };

    public void showIllegalArgumentDialog(String message) {
        if (illegalArgumentDialog == null) {
            illegalArgumentDialog = new AlertDialog.Builder(this)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            illegalArgumentDialog.dismiss();
                        }
                    })
                    .setTitle("提示")
                    .create();
        }
        illegalArgumentDialog.dismiss();
        illegalArgumentDialog.setMessage(message);
        illegalArgumentDialog.show();
    }

    private OnLiveRecordErrorListener mOnErrorListener = new OnLiveRecordErrorListener() {
        @Override
        public void onError(int errorCode) {
            switch (errorCode) {
                case AlivcStatusCode.ERROR_ILLEGAL_ARGUMENT:
                    showIllegalArgumentDialog("推流地址有误");
                case AlivcStatusCode.ERROR_SERVER_CLOSED_CONNECTION:
                    //发生违法操作时，服务器会主动断开链接
                    addView(new TextView(LiveCameraActivity.this), "服务器断开连接");
                case AlivcStatusCode.ERORR_OUT_OF_MEMORY:
                    //手机内存不足时导致底层某些内存开辟失败引起该错误
                    addView(new TextView(LiveCameraActivity.this), "内存不足");
                    break;
                case AlivcStatusCode.ERROR_CONNECTION_TIMEOUT:
                    //网络较差时导致链接超时或者数据发送超时
                    addView(new TextView(LiveCameraActivity.this), "网络超时");
                    break;
                case AlivcStatusCode.ERROR_BROKEN_PIPE:
                    //	推流时进行了违法操作，比如同时推流同一个地址，或者重复推流，服务器端会主动关闭socket，引起broken pipe
                    addView(new TextView(LiveCameraActivity.this), "推流中断");
                    break;
                case AlivcStatusCode.ERROR_IO:
                    //导致该错误的情况比较多，比如网络环境较差或者推流域名错误等导致DNS解析失败等
                    addView(new TextView(LiveCameraActivity.this), "I/O错误");
                    break;
                case AlivcStatusCode.ERROR_NETWORK_UNREACHABLE:
                    //该错误通常发生在网络无法传输数据的情况，或者推流过程网络中断等情况
                    addView(new TextView(LiveCameraActivity.this), "网络情况差");
                    break;

                default:
            }
        }
    };

    private AlivcEventResponse mBitrateUpRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_CURR_BITRATE);
//            Log.d(TAG, "event->up bitrate, previous bitrate is " + preBitrate +
//                    "current bitrate is " + currBitrate);
        }
    };
    private AlivcEventResponse mBitrateDownRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_CURR_BITRATE);
//            Log.d(TAG, "event->down bitrate, previous bitrate is " + preBitrate +
//                    "current bitrate is " + currBitrate);
        }
    };
    private AlivcEventResponse mAudioCaptureSuccRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //成功打开音频
        }
    };

    private AlivcEventResponse mVideoEncoderSuccRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //成功打开视频

        }
    };
    private AlivcEventResponse mVideoEncoderFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //视频编码失败
        }
    };
    private AlivcEventResponse mVideoEncodeFrameFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //视频帧编码失败
        }
    };


    private AlivcEventResponse mInitDoneRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //直播录制初始化完成
        }
    };

    private AlivcEventResponse mDataDiscardRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int discardFrames = 0;
            if (bundle != null) {
                discardFrames = bundle.getInt(AlivcEvent.EventBundleKey.KEY_DISCARD_FRAMES);
            }
//            Log.d(TAG, "event->data discard, the frames num is " + discardFrames);
        }
    };

    private AlivcEventResponse mAudioCaptureOpenFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //音频捕捉设备打开失败
        }
    };

    private AlivcEventResponse mAudioEncodeFrameFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            //音频编码失败
        }
    };

    public void addView(TextView childView, String text) {
        if (push_state_ll.getChildCount() > 3) {
            push_state_ll.removeViewAt(0);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 10);
        childView.setTextColor(Color.RED);
        childView.setText(text);
        childView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        childView.setBackgroundResource(R.drawable.text_state);
        childView.setLayoutParams(layoutParams);
        childView.setPadding(15, 10, 15, 10);
        childView.setMaxEms(20);
        childView.setMaxLines(3);
        push_state_ll.addView(childView);
    }

    private void showPopPro() {
        push_state_ll.setVisibility(View.INVISIBLE);
        View pop_products_layout = LayoutInflater.from(this).inflate(R.layout.pop_pro_layout, null);
        pop_products = new CustomPopupWindow.Builder(this)
                .setView(pop_products_layout)
                .setWidthAndHeight(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setAnimationStyle(R.style.AnimationFade)
                .create();

        pop_products.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int visible = push_state ? View.VISIBLE : View.INVISIBLE;
                push_state_ll.setVisibility(visible);
            }
        });
        pop_products_listview = (ListView) pop_products_layout.findViewById(R.id.lv_pop_products);
        Map<String, String> params = new HashMap<String, String>();
        params.put("liveid", mShareInfo.getLiveId());
        params.put("op", "goods");
        ProductsAdapter productsAdapter = new ProductsAdapter(pList, this, eventUrl + "applogin", params);
        pop_products_listview.setAdapter(productsAdapter);
        View ll = LayoutInflater.from(this).inflate(R.layout.activity_live_camera, null);
        pop_products.showAtLocation(ll, Gravity.LEFT, 0, 0);
        pop_products_layout.findViewById(R.id.c_pop_pro_empty_tip).setVisibility(pList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private int measuredWidth = 0;
    private int measureHeight = 0;

    private void showSettingPop() {
        if (mPop_settings == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.pop_settings_layout, null);
            mPop_settings = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mPop_settings.setFocusable(true);
            mPop_settings.setBackgroundDrawable(new BitmapDrawable());
            mPop_settings.setOutsideTouchable(true);
            mPop_settings.setAnimationStyle(R.style.SettingsAnimation);
            mPop_settings.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            measureHeight = mPop_settings.getContentView().getMeasuredHeight();
            measuredWidth = mPop_settings.getContentView().getMeasuredWidth();
            mPop_settings.showAsDropDown(settings, -measuredWidth, -(settings.getHeight() + measureHeight + 1));

            mLl_share = (LinearLayout) view.findViewById(R.id.ll_share);
            mLl_share.setOnClickListener(this);
            mLl_camera = (LinearLayout) view.findViewById(ll_camera);
            mLl_camera.setOnClickListener(this);
            mLl_sound = (LinearLayout) view.findViewById(R.id.ll_sound);
            mLl_sound.setOnClickListener(this);
            mLl_beauty = (LinearLayout) view.findViewById(R.id.ll_beauty);
            mLl_beauty.setOnClickListener(this);
            mLl_flash = (LinearLayout) view.findViewById(R.id.ll_flash);
            mLl_flash.setOnClickListener(this);
            tv_sound = (TextView) view.findViewById(R.id.tv_sound_text);
            iv_sound = (ImageView) view.findViewById(R.id.iv_sound_icon);
            iv_sound.setImageResource(R.drawable.button_volume_on);
            tv_beauty = (TextView) view.findViewById(R.id.tv_beauty_text);
            iv_beauty = (ImageView) view.findViewById(R.id.iv_beauty_icon);
            iv_beauty.setImageResource(R.drawable.no_beauty);
            iv_flash = (ImageView) view.findViewById(R.id.iv_flash_icon);
            tv_flash = (TextView) view.findViewById(R.id.tv_flash_text);
            iv_flash.setImageResource(R.drawable.button_light_off);
        } else {
            mPop_settings.showAsDropDown(settings, -measuredWidth, -(settings.getHeight() + measureHeight + 1));
        }
    }

    private void showPopTalk() {
        if (pop_comment != null && pop_comment.isShowing()) {
            pop_comment.dismiss();
        } else {
            push_state_ll.setVisibility(View.INVISIBLE);
            View ll = LayoutInflater.from(this).inflate(R.layout.activity_live_camera, null);
            pop_comment.showAtLocation(ll, Gravity.LEFT, 0, 0);
        }
    }

    public void showSharePop() {
        mSharePopupWindow = new CustomPopupWindow.Builder(this)
                .setView(R.layout.pop_camera_share_layout)
                .setWidthAndHeight(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.Settings_share_animation)
                .setOutsideTouchable(true)
                .setViewOnclickListener(this)
                .create();
        mSharePopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredWidths = mSharePopupWindow.getContentView().getMeasuredWidth();
        mSharePopupWindow.showAsDropDown(settings, -(measuredWidth + measuredWidths + 30), -(settings.getHeight() + measureHeight));

    }

    @Override
    public void getChildView(View view, int layoutResId) {
        switch (layoutResId) {
            case R.layout.pop_camera_share_layout:
                view.findViewById(R.id.c_ll_share_wx).setOnClickListener(this);
                view.findViewById(R.id.c_ll_share_pyq).setOnClickListener(this);
                break;
        }
    }
}
