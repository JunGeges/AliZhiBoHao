package com.zmtmt.zhibohao.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bumptech.glide.request.target.ViewTarget;
import com.duanqu.qupai.jni.ApplicationGlue;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zmtmt.zhibohao.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/28.
 */
public class MyApplication extends Application {
    private static final String TAG = "ZHIBOHAO";
    public static IWXAPI api;
    //微信配置信息
    public static final String APP_ID = "wx19674a62b3628c8f";
    public static final String APPSECRET = "73259c1134e73bdcc73c07400f574890";
    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    public static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
    public static final String URL_USERINFO = "https://api.weixin.qq.com/sns/userinfo?";
    public static boolean isInstallWx = false;
    public static ArrayList<Activity> list = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
        isInstallWx = isWXAppInstalledAndSupported();

        //alibaba
        System.loadLibrary("gnustl_shared");
//        System.loadLibrary("ijkffmpeg");//目前使用微博的ijkffmpeg会出现1K再换wifi不重连的情况
        System.loadLibrary("qupai-media-thirdparty");
//        System.loadLibrary("alivc-media-jni");
        System.loadLibrary("qupai-media-jni");
        ApplicationGlue.initialize(this);

        //Bugly SDK初始化
        CrashReport.initCrashReport(getApplicationContext(), "3080098224", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private boolean isWXAppInstalledAndSupported() {
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(APP_ID);
        boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }
}
