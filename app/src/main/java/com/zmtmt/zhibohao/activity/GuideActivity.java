package com.zmtmt.zhibohao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zmtmt.zhibohao.R;

public class GuideActivity extends Activity {
    private static final String TAG = "GuideActivity";
    private static final int LOGIN = 0;
    private static final int SKIP_LOGIN = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN:
                    Intent intent_index = new Intent(GuideActivity.this, IndexActivity.class);
                    startActivity(intent_index);
                    finish();
                    break;

                case SKIP_LOGIN:
                    Intent intent_web=new Intent(GuideActivity.this,WebActivity.class);
                    startActivity(intent_web);
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        SharedPreferences preferences = getSharedPreferences("unionId", GuideActivity.MODE_PRIVATE);
        String unionId = preferences.getString("unionId", "");
        Log.d(TAG, "unionId:"+unionId);
        if (TextUtils.isEmpty(unionId)) {
            Message msg_login = Message.obtain();
            msg_login.what = LOGIN;
            mHandler.sendMessageDelayed(msg_login, 2000);
        } else {
            Message msg_skip_login = Message.obtain();
            msg_skip_login.what = SKIP_LOGIN;
            mHandler.sendMessageDelayed(msg_skip_login, 2000);
        }

    }
}