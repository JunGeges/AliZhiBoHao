package com.zmtmt.zhibohao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zmtmt.zhibohao.entity.ShareInfo;
import com.zmtmt.zhibohao.tools.ShareUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class endActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextViewTime, mTextViewPersons;
    private Button mButtonConfirm;
    private ImageView mImageViewWX, mImageViewPyq;
    private final int WXSceneTimeline = 1;
    private final int WXSceneSession = 2;
    private ShareInfo mShareInfo;
    private long mLiveTime;
    private String mLivePersons;
    private CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        getData();
        initViews();
    }

    public void getData() {
        Intent intent = getIntent();
        mLiveTime = intent.getLongExtra("livetime", 0);
        mLivePersons = intent.getStringExtra("livepersons");
        mShareInfo = intent.getParcelableExtra("shareinfo");
    }

    private void initViews() {
        mTextViewTime = (TextView) findViewById(R.id.end_tv_live_time);
        mTextViewPersons = (TextView) findViewById(R.id.end_tv_persons);
        mButtonConfirm = (Button) findViewById(R.id.end_btn_confirm);
        mButtonConfirm.setOnClickListener(this);
        mImageViewPyq = (ImageView) findViewById(R.id.end_iv_pyq);
        mImageViewWX = (ImageView) findViewById(R.id.end_iv_wx);
        mImageViewPyq.setOnClickListener(this);
        mImageViewWX.setOnClickListener(this);
        String liveTime = formatTime(mLiveTime);
        mTextViewTime.setText(liveTime);
        mTextViewPersons.setText(mLivePersons);
        mCircleImageView = (CircleImageView) findViewById(R.id.head_view);
        SharedPreferences sp = getSharedPreferences("WXUserParams", MODE_PRIVATE);
        String wxLogoUrl = sp.getString("WXLogoUrl", null);
        Glide.with(this).load(wxLogoUrl).into(mCircleImageView);
    }

    @NonNull
    private String formatTime(long time) {
        String hour = "";
        String minute = "";
        String second = "";
        String liveTime = "";
        if (time >= 3600) {
            long h = time / 3600;
            long temp = time % 3600;
            long m = temp / 60;
            long s = temp % 60;
            hour = h < 10 ? "0" + String.valueOf(h) : String.valueOf(h);
            minute = m < 10 ? "0" + String.valueOf(m) : String.valueOf(m);
            second = s < 10 ? "0" + String.valueOf(s) : String.valueOf(s);
        } else if (time >= 60) {
            long m = time / 60;
            long s = time % 60;
            hour = "00";
            minute = m < 10 ? "0" + String.valueOf(m) : String.valueOf(m);
            second = s < 10 ? "0" + String.valueOf(s) : String.valueOf(s);
        } else {
            hour = "00";
            minute = "00";
            second = time < 10 ? "0" + String.valueOf(time) : String.valueOf(time);
        }
        liveTime = hour + ":" + minute + ":" + second;
        return liveTime;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.end_btn_confirm:
                finish();
                break;

            case R.id.end_iv_wx:
                ShareUtils.shareToWX(mShareInfo, WXSceneSession);
                break;

            case R.id.end_iv_pyq:
                ShareUtils.shareToWX(mShareInfo, WXSceneTimeline);
                break;
        }
    }
}
