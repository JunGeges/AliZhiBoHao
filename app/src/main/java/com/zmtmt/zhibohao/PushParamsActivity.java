package com.zmtmt.zhibohao;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.alibaba.livecloud.model.AlivcWatermark;
import com.orhanobut.logger.Logger;
import com.zmtmt.zhibohao.entity.Products;
import com.zmtmt.zhibohao.entity.ShareInfo;
import com.zmtmt.zhibohao.widget.CustomPopupWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.duanqu.qupai.recorder.RecorderTask.TAG;

public class PushParamsActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button connectBT;
    private RadioGroup resolutionCB;
    private RadioButton resolution360button;
    private RadioButton resolution480button;
    private RadioButton resolution720button;
    private RadioButton resolution540button;
    private RadioGroup rotationGroup;
    private RadioButton screenOrientation1;
    private RadioButton screenOrientation2;

    private ImageView pickDirectory;
    private TextView mSeekBarlv;
    private TextView zhenSeekBarlv;
    private SeekBar sb_zlv;
    private SeekBar sb_mlv;
    private RelativeLayout mRelativeLayout;
    private TextView tv_cancel;

    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int GET_PHOTO = 3;
    private Uri headImgUri;
    private String eventUrl;//请求地址
    private String openID;//直播用户openid
    private String pushUrl;//推流地址
    private int memberlevelId;//会员等级
    private ArrayList<Products> productList;//商品集合
    private ShareInfo mShareInfo;//分享信息
    private String watermarkUrl = "assets://wartermark/wglogo.png";//默认微谷logo
    private int site = 1;//默认水印位置上右
    private int initBitrate = 1200;//初始化码率
    private int bestBitrate = 1200;//最佳码率
    private int maxBitrate = 1300;
    int frameRate = 30;//帧率
    private Uri mUriFile;
    private Button btn_left_top, btn_left_bottom, btn_right_top, btn_right_bottom;
    private ImageView mImageView;
    private TextView mTextView;
    private boolean isOpen;
    private LinearLayout mLinearLayout;
    private CustomPopupWindow mCustomPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushparams);
        initViews();
        getExternalData();
    }

    private void initViews() {
        mImageView = (ImageView) findViewById(R.id.iv_speed);
        mTextView = (TextView) findViewById(R.id.tv_speed);
        mTextView.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_speed_layout);

        btn_left_top = (Button) findViewById(R.id.btn_left_top);
        btn_left_bottom = (Button) findViewById(R.id.btn_left_bottom);
        btn_right_bottom = (Button) findViewById(R.id.btn_right_bottom);
        btn_right_top = (Button) findViewById(R.id.btn_right_top);
        btn_left_bottom.setOnClickListener(this);
        btn_right_top.setOnClickListener(this);
        btn_left_top.setOnClickListener(this);
        btn_right_bottom.setOnClickListener(this);
        connectBT = (Button) findViewById(R.id.connectBT);
        connectBT.setOnClickListener(this);
        resolutionCB = (RadioGroup) findViewById(R.id.resolution_group);
        resolution360button = (RadioButton) findViewById(R.id.radio_smooth_definition);
        resolution480button = (RadioButton) findViewById(R.id.radio_standard_definition);
        resolution540button = (RadioButton) findViewById(R.id.radio_high_definition);
        resolution720button = (RadioButton) findViewById(R.id.radio_super_definition);

        rotationGroup = (RadioGroup) findViewById(R.id.rotation_group);
        screenOrientation1 = (RadioButton) findViewById(R.id.screenOrientation1);
        screenOrientation2 = (RadioButton) findViewById(R.id.screenOrientation2);
        resolutionCB.setOnCheckedChangeListener(this);
        rotationGroup.setOnCheckedChangeListener(this);

        pickDirectory = (ImageView) findViewById(R.id.iv_pick_directory);
        pickDirectory.setOnClickListener(this);

        mSeekBarlv = (TextView) findViewById(R.id.tv_mlv);
        mSeekBarlv.setText("(1200)");
        zhenSeekBarlv = (TextView) findViewById(R.id.tv_zhenlv);
        zhenSeekBarlv.setText("(30)");

        sb_mlv = (SeekBar) findViewById(R.id.sb_mlv);
        sb_mlv.setProgress(1200);

        sb_zlv = (SeekBar) findViewById(R.id.sb_zlv);
        sb_zlv.setProgress(30);

        sb_mlv.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarlv.setText("(" + progress + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //获取最终的码率
                int progress = seekBar.getProgress();
                initBitrate = progress;
            }
        });
        sb_zlv.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zhenSeekBarlv.setText("(" + progress + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //获取最终的帧率
                int progress = seekBar.getProgress();
                frameRate = progress;
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_back);
        mRelativeLayout.setOnClickListener(this);
    }

    public void getExternalData() {
        Intent intent = getIntent();
        pushUrl = intent.getStringExtra("pushurl");
//        pushUrl=" rtmp://video-center.alivecdn.com/live/123?vhost=ali.zipindao.tv";//阿里流
//        pushUrl="rtmp://appa-push.zipindao.tv/live/110";//网宿流
        eventUrl = intent.getStringExtra("eventurl");
        openID = intent.getStringExtra("openid");
        memberlevelId = Integer.parseInt(intent.getStringExtra("memberlevelid"));
        Logger.t(TAG).d(eventUrl + "liveconsumeajax" + pushUrl);
        productList = intent.getParcelableArrayListExtra("products_list");
        mShareInfo = intent.getParcelableExtra("shareinfo");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int checkedRadioButtonId = group.getCheckedRadioButtonId();
        switch (checkedRadioButtonId) {
            case R.id.radio_smooth_definition:
                //360p
                mSeekBarlv.setText(600 + "");
                sb_mlv.setProgress(600);
                zhenSeekBarlv.setText("(25)");
                sb_zlv.setProgress(25);
                initBitrate = 600;
                bestBitrate = 600;
                maxBitrate = 800;
                frameRate = 25;
                resolution360button.setBackgroundResource(R.drawable.push_params_set_click_btn);
                resolution480button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution720button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution540button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                break;
            case R.id.radio_standard_definition:
                //480p
                mSeekBarlv.setText(800 + "");
                sb_mlv.setProgress(800);
                zhenSeekBarlv.setText("(25)");
                sb_zlv.setProgress(25);
                initBitrate = 800;
                bestBitrate = 800;
                maxBitrate = 1000;
                frameRate = 25;
                resolution360button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution480button.setBackgroundResource(R.drawable.push_params_set_click_btn);
                resolution720button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution540button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                break;
            case R.id.radio_super_definition:
                //720p
                mSeekBarlv.setText(1800 + "");
                sb_mlv.setProgress(1800);
                zhenSeekBarlv.setText("(30)");
                sb_zlv.setProgress(30);
                initBitrate = 1800;
                bestBitrate = 1800;
                maxBitrate = 2500;
                frameRate = 30;
                resolution360button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution480button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution720button.setBackgroundResource(R.drawable.push_params_set_click_btn);
                resolution540button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                break;
            case R.id.radio_high_definition:
                //540p
                mSeekBarlv.setText(1200 + "");
                sb_mlv.setProgress(1200);
                zhenSeekBarlv.setText("(30)");
                sb_zlv.setProgress(30);
                initBitrate = 1200;
                bestBitrate = 1500;
                frameRate = 30;
                resolution360button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution480button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution720button.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                resolution540button.setBackgroundResource(R.drawable.push_params_set_click_btn);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectBT:
                int videoResolution = 0;
                int cameraFrontFacing = AlivcMediaFormat.CAMERA_FACING_BACK;
                boolean screenOrientation;
                if (resolution360button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_360P;
                } else if (resolution480button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_480P;
                } else if (resolution720button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_720P;
                } else if (resolution540button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_540P;
                }
                if (screenOrientation1.isChecked()) {
                    screenOrientation = true;
                } else {
                    screenOrientation = false;
                }

                int dx = 14;
                int dy = 14;
                int minBitrate = 500;
                LiveCameraActivity.RequestBuilder builder = new LiveCameraActivity.RequestBuilder()
                        .bestBitrate(bestBitrate)
                        .cameraFacing(cameraFrontFacing)
                        .dx(dx)
                        .dy(dy)
                        .site(site)
                        .rtmpUrl(pushUrl)
                        .videoResolution(videoResolution)
                        .portrait(screenOrientation)
                        .watermarkUrl(watermarkUrl)
                        .minBitrate(minBitrate)
                        .maxBitrate(maxBitrate)
                        .frameRate(frameRate)
                        .initBitrate(initBitrate)
                        .memberlevelId(memberlevelId)
                        .eventUrl(eventUrl)
                        .productList(productList)
                        .shareInfo(mShareInfo)
                        .openId(openID);
                LiveCameraActivity.startActivity(v.getContext(), builder);
                break;

            case R.id.iv_pick_directory:
                showPopWindow();
                break;

            case R.id.tv_cancel:
                mCustomPopupWindow.dismiss();
                break;

            case R.id.tv_choose_photo:
                if (ContextCompat.checkSelfPermission(PushParamsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PushParamsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;

            case R.id.tv_take_photo:
                takePhotos();
                break;

            case R.id.rl_back:
                finish();
                break;

            case R.id.btn_left_top:
                btn_left_top.setBackgroundResource(R.drawable.push_params_set_click_btn);
                btn_left_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                site = AlivcWatermark.SITE_TOP_LEFT;
                break;

            case R.id.btn_left_bottom:
                btn_left_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_left_bottom.setBackgroundResource(R.drawable.push_params_set_click_btn);
                btn_right_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                site = AlivcWatermark.SITE_BOTTOM_LEFT;
                break;

            case R.id.btn_right_top:
                btn_left_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_left_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_top.setBackgroundResource(R.drawable.push_params_set_click_btn);
                btn_right_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                site = AlivcWatermark.SITE_TOP_RIGHT;
                break;

            case R.id.btn_right_bottom:
                btn_left_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_left_bottom.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_top.setBackgroundResource(R.drawable.push_params_set_normal_btn);
                btn_right_bottom.setBackgroundResource(R.drawable.push_params_set_click_btn);
                site = AlivcWatermark.SITE_BOTTOM_RIGHT;
                break;

            case R.id.iv_speed:
                if (!isOpen) {
                    startAnimation();
                    mTextView.setVisibility(View.VISIBLE);
                    isOpen = true;
                } else {
                    startAnimation();
                    isOpen = false;
                }
                break;

            case R.id.tv_speed:
                Intent intent = new Intent(this, SpeedTestActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showPopWindow() {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setView(R.layout.pop_pick_directory)
                .setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setAnimationStyle(R.style.ShareAnimation)
                .setBackGroundLevel(0.5f)
                .setViewOnclickListener(new CustomPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                        TextView choose_photo = (TextView) view.findViewById(R.id.tv_choose_photo);
                        TextView take_photo = (TextView) view.findViewById(R.id.tv_take_photo);
                        choose_photo.setOnClickListener(PushParamsActivity.this);
                        tv_cancel.setOnClickListener(PushParamsActivity.this);
                        take_photo.setOnClickListener(PushParamsActivity.this);
                    }
                })
                .create();
        mCustomPopupWindow.showAtLocation(LayoutInflater.from(PushParamsActivity.this).inflate(R.layout.activity_webview, null), Gravity.BOTTOM, 0, 0);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, GET_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void takePhotos() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            headImgUri = Uri.fromFile(outputImage);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, outputImage.getAbsolutePath());
            headImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        // 启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, headImgUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 裁剪
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // 裁剪框的比例，2：1
        intent.putExtra("aspectX", 2);// 输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小,不能太大500程序崩溃
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 50);
        // 图片格式
        /* intent.putExtra("outputFormat", "JPEG"); */
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
        // 同一个地址下 裁剪的图片覆盖拍照的图片
        mUriFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory() + "/tmp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriFile);
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_PHOTO:
                if (resultCode == RESULT_OK) {
                    crop(data.getData());
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    crop(headImgUri);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap cropBitmap = data.getParcelableExtra("data");
                    String path = mUriFile.getPath();
                    watermarkUrl = path;
                    pickDirectory.setImageBitmap(cropBitmap);
                    mCustomPopupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void startAnimation() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        if (!isOpen) {
            ObjectAnimator objectAnimatorT = ObjectAnimator.ofFloat(mLinearLayout, "translationX", 210, 0);
            ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(mTextView, "Alpha", 0, 1);
            set.playTogether(objectAnimatorT, objectAnimatorA);
            set.start();
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mImageView.setImageResource(R.drawable.smooth_out);
                }
            });
        } else {
            ObjectAnimator objectAnimatorT = ObjectAnimator.ofFloat(mLinearLayout, "translationX", 0, 210);
            ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(mTextView, "Alpha", 1, 0);
            set.playTogether(objectAnimatorT, objectAnimatorA);
            set.start();
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mImageView.setImageResource(R.drawable.smooth_enter);
                    mTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
