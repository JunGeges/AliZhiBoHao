package com.zmtmt.zhibohao.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.zmtmt.zhibohao.R;
import com.zmtmt.zhibohao.app.MyApplication;
import com.zmtmt.zhibohao.tools.CommonUtils;

import java.io.IOException;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_login;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    int position = 0;
    private TextView mProtocol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        MyApplication.list.add(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mProtocol = (TextView) findViewById(R.id.tv_protocol);
        mProtocol.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.btn_wx_login);
        surfaceView = (SurfaceView) findViewById(R.id.index_surfaceView);
        btn_login.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        //默认为false  设置为true的时候 允许surface被显示的时候是否启用或者禁用屏幕保持打开状态
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (position== 0) {
                    try {
                        play();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });
    }

    public void play() throws IllegalArgumentException, SecurityException,
            IllegalStateException, IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AssetFileDescriptor fd = this.getAssets().openFd("index.mp4");
        mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
                fd.getLength());
        mediaPlayer.setLooping(true);
        mediaPlayer.setDisplay(surfaceView.getHolder());
        // 通过异步的方式装载媒体资源
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 装载完毕回调
                mediaPlayer.start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wx_login:
                if (MyApplication.isInstallWx) {
                    btn_login.setText(R.string.wx_logining);
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = getString(R.string.scope);
                    req.state = getString(R.string.state);
                    MyApplication.api.sendReq(req);
                } else {
                    CommonUtils.showToast(getApplicationContext(), getString(R.string.wx_not_install));
                }
                break;

            case R.id.tv_protocol:
                Intent intent = new Intent(IndexActivity.this, ProtocolActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        btn_login.setText(R.string.wx_authorization_login);
    }


}
