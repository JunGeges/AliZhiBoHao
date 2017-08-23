package com.zmtmt.zhibohao.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zmtmt.zhibohao.BuildConfig;
import com.zmtmt.zhibohao.R;
import com.zmtmt.zhibohao.app.MyApplication;
import com.zmtmt.zhibohao.entity.LiveRoomInfo;
import com.zmtmt.zhibohao.entity.Products;
import com.zmtmt.zhibohao.entity.ShareInfo;
import com.zmtmt.zhibohao.tools.CommonUtils;
import com.zmtmt.zhibohao.tools.HttpUtils;
import com.zmtmt.zhibohao.tools.ShareUtils;
import com.zmtmt.zhibohao.widget.CustomPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.webkit.WebSettings.LOAD_NO_CACHE;


/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */

@SuppressLint("JavascriptInterface")
public class WebActivity extends AppCompatActivity implements View.OnClickListener, CustomPopupWindow.ViewInterface, View.OnLongClickListener {
    private static final String TAG = "WebActivity";
    private static final String URL = "http://www.zipindao.tv/app/index.php?c=entry&m=wg_test&i=4&do=applogin";
    private static final String INDEXURL = "http://www.zipindao.tv/app/index.php?c=entry&m=wg_test&do=myroomlive&i=4";
    private WebView mWebView;
    private ValueCallback<Uri> mUploadMessage;//回调图片选择，4.4以下
    private ValueCallback<Uri[]> mUploadCallbackAboveL;//回调图片选择，5.0以上
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ProgressBar mProgressBar;
    private RelativeLayout rl_back, rl_option;
    private TextView tv_url_title;
    private LiveRoomInfo lri;//直播房间信息
    private Products products;
    private ArrayList<Products> pList;
    private static final int WXSCENETIMELINE = 1;//朋友圈
    private static final int WXSCENESESSION = 2;//好友
    private int versionCode;//app版本号
    private TextView tv_cancel;
    private ShareInfo shareInfo;
    private List<ShareInfo> sList = new ArrayList<>();
    private long exitTime = 0;
    private String mBase;
    private FrameLayout mFrameLayout;
    private CustomPopupWindow mCustomPopupWindow_logff;
    private CustomPopupWindow mCustomPopupWindow_option;
    private CustomPopupWindow mCustomPopupWindow_share;
    private Bitmap mBitmap;
    private AlertDialog alertDialog;
    private BroadcastReceiver upgradeReceiver;//版本更新广播
    private static final String UPGRADE_URL = "http://app.zmtmt.com/down/zhibohao.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        checkVersion();
        MyApplication.list.add(this);
        initViews();
        initEvent();
    }

    private void initViews() {
        versionCode = BuildConfig.VERSION_CODE;
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        mWebView = new WebView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        mFrameLayout.addView(mWebView);
        tv_url_title = (TextView) findViewById(R.id.tv_url_title);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_option = (RelativeLayout) findViewById(R.id.rl_option);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initEvent() {
        rl_back.setOnClickListener(this);
        rl_option.setOnClickListener(this);
        WebSettings settings = mWebView.getSettings();
        //支持javascript
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsToJava(), "app");
        //设置支持缩放
        settings.setSupportZoom(true);//支持缩放
        settings.setBuiltInZoomControls(true);//设置隐藏缩放按钮
        settings.setDisplayZoomControls(false);//隐藏原生的缩放按钮

        //设置自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //屏幕自适应网页  解决低分辨率可能会显示异常的问题
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //设置缓存机制
        //settings.setAppCacheEnabled(true);
        settings.setCacheMode(LOAD_NO_CACHE);

        //运行webview通过URI获取安卓文件
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setOnLongClickListener(this);
        mWebView.loadUrl(URL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (mCustomPopupWindow_share != null && mCustomPopupWindow_share.isShowing()) {
                    mCustomPopupWindow_share.dismiss();
                }
                break;

            case R.id.rl_back:
                if (mWebView.canGoBack()) {
                    if (INDEXURL.equals(mWebView.getUrl())) {
                        CommonUtils.showToast(WebActivity.this, getString(R.string.w_tip));
                    } else {
                        mWebView.goBack();
                    }
                } else {
                    CommonUtils.showToast(WebActivity.this, getString(R.string.w_tip));
                }
                break;

            case R.id.rl_option:
                showOptionPopWindow();
                break;

            case R.id.tv_share:
                showSharePopWindow();
                break;

            case R.id.tv_logoff:
                showLogoffWindow();
                break;

            case R.id.tv__logoff_cancel:
                if (mCustomPopupWindow_logff != null && mCustomPopupWindow_logff.isShowing()) {
                    mCustomPopupWindow_logff.dismiss();
                }
                break;

            case R.id.tv_confirm:
                logOff();
                break;

            case R.id.ll__share_circle:
                if (sList.size() != 0) {
                    ShareUtils.shareToWX(shareInfo, WXSCENETIMELINE, mBitmap);
                } else {
                    CommonUtils.showToast(WebActivity.this, getString(R.string.w_shareTip));
                }
                break;

            case R.id.ll_share_friend:
                if (sList.size() != 0) {
                    ShareUtils.shareToWX(shareInfo, WXSCENESESSION, mBitmap);
                } else {
                    CommonUtils.showToast(WebActivity.this, getString(R.string.w_shareTip));
                }
                break;
        }
    }

    //注销功能
    private void logOff() {
        SharedPreferences.Editor sp = getSharedPreferences("unionId", WebActivity.MODE_PRIVATE).edit();
        sp.clear();
        sp.apply();
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    //确认是否注销弹窗
    private void showLogoffWindow() {
        if (mCustomPopupWindow_logff != null && mCustomPopupWindow_logff.isShowing()) return;
        mCustomPopupWindow_logff = new CustomPopupWindow.Builder(this)
                .setView(R.layout.logoff_layout)
                .setWidthAndHeight(850, WindowManager.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setBackGroundLevel(0.5f)
                .setViewOnclickListener(this)
                .create();
        mCustomPopupWindow_logff.showAtLocation(mWebView, Gravity.CENTER, 0, 0);
    }

    //注销选项弹窗
    private void showOptionPopWindow() {
        if (mCustomPopupWindow_option != null && mCustomPopupWindow_option.isShowing()) return;
        mCustomPopupWindow_option = new CustomPopupWindow.Builder(this)
                .setView(R.layout.pop_option_layout)
                .setWidthAndHeight(400, WindowManager.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setViewOnclickListener(this)
                .create();
        mCustomPopupWindow_option.showAsDropDown(rl_option);
    }

    //分享弹窗
    public void showSharePopWindow() {
        if (mCustomPopupWindow_share != null && mCustomPopupWindow_share.isShowing()) return;
        mCustomPopupWindow_share = new CustomPopupWindow.Builder(this)
                .setView(R.layout.pop_share_layout)
                .setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true)
                .setViewOnclickListener(this)
                .setBackGroundLevel(0.5f)
                .setAnimationStyle(R.style.ShareAnimation)
                .create();
        mCustomPopupWindow_share.showAtLocation(LayoutInflater.from(WebActivity.this).inflate(R.layout.activity_webview, null), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void getChildView(View view, int layoutResId) {
        switch (layoutResId) {
            case R.layout.pop_option_layout:
                TextView tv_share = (TextView) view.findViewById(R.id.tv_share);
                TextView tv_logoff = (TextView) view.findViewById(R.id.tv_logoff);
                tv_share.setOnClickListener(this);
                tv_logoff.setOnClickListener(this);
                break;

            case R.layout.logoff_layout:
                view.findViewById(R.id.tv__logoff_cancel).setOnClickListener(this);
                view.findViewById(R.id.tv_confirm).setOnClickListener(this);
                break;

            case R.layout.pop_share_layout:
                LinearLayout ll__share_circle = (LinearLayout) view.findViewById(R.id.ll__share_circle);
                LinearLayout ll_share_friend = (LinearLayout) view.findViewById(R.id.ll_share_friend);
                tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                ll__share_circle.setOnClickListener(this);
                ll_share_friend.setOnClickListener(this);
                tv_cancel.setOnClickListener(WebActivity.this);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        //屏蔽长按事件
        return true;
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.i(TAG, url);
            return true;
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            sList.clear();
            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            mProgressBar.setVisibility(View.GONE);
            tv_url_title.setText(webView.getTitle());
        }

        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webView, errorCode, description, failingUrl);
            CommonUtils.showToast(WebActivity.this, getString(R.string.check_net));
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        // For Android <3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        // For Android >=3.0
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        //For Android >=4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        // For Android >=5.0
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            if (mWebView.getUrl().equals(INDEXURL)) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    CommonUtils.showToast(this, getString(R.string.w_exit_app));
                    exitTime = System.currentTimeMillis();
                } else {
                    for (int i = 0; i < MyApplication.list.size(); i++) {
                        MyApplication.list.get(i).finish();
                    }
                }
            } else {
                mWebView.goBack();
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (mCustomPopupWindow_logff != null && mCustomPopupWindow_logff.isShowing())
            mCustomPopupWindow_logff.dismiss();
        if (mCustomPopupWindow_option != null && mCustomPopupWindow_option.isShowing())
            mCustomPopupWindow_option.dismiss();

        //销毁WebView
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private String strResponse;

    //以下针对更新升级
    private void checkVersion() {
        new UpdateVersionTask().execute(UPGRADE_URL);
    }

    int serviceVersionCode = 0;

    class UpdateVersionTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpUtils.get(strings[0], new HttpUtils.NetWorkStatus() {
                @Override
                public void onSuccessful(String response) {
                    strResponse = response;
                }

                @Override
                public void onFailed(String error) {

                }
            });
            return strResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            StringBuilder builder = new StringBuilder();
            boolean hasVersionCode = false;
            String url = "";
            try {
                if (!TextUtils.isEmpty(s)) {
                    JSONObject versionInfo = new JSONObject(s);
                    serviceVersionCode = versionInfo.getInt("versionCode");
                    //上传的格式是(更新内容-更新内容-更新内容)
                    String updateContent =versionInfo.getString("desc");
                    String[] contents = updateContent.split("-");
                    for (int i = 0; i < contents.length; i++) {
                        builder.append((i+1)+"."+contents[i]+"\n");
                    }
                    url = versionInfo.getString("url");
                    hasVersionCode = true;
                }
            } catch (JSONException ignored) {
                ignored.printStackTrace();
            }
            final String updateUrl = url;

            if (hasVersionCode && BuildConfig.VERSION_CODE < serviceVersionCode) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WebActivity.this);
                alertDialogBuilder.setCancelable(false);
                alertDialog = alertDialogBuilder.setTitle(getString(R.string.check_version))
                        .setMessage(builder.toString())
                        .setPositiveButton(getString(R.string.button_positive), null)
//                        .setNegativeButton(getString(R.string.button_cancel), null)
                        .create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                WebActivity.this.update(updateUrl);
//                                alertDialog.dismiss();
                                CommonUtils.showToast(WebActivity.this,getString(R.string.app_download));
                            }
                        });
                    }
                });
                alertDialog.show();
            }
            super.onPostExecute(s);
        }
    }

    public void update(String url) {
        downloadAndInstall(url);
    }

    private void downloadAndInstall(String url) {
        final DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("zmtmt", "zmtmt_zhibohao_update");
        request.setDescription(getString(R.string.new_version_download));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();
        }
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);
        final long reference = dManager.enqueue(request);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        upgradeReceiver = new UpgradeBroadcastReceiver(dManager, reference);
        registerReceiver(upgradeReceiver, filter);
    }

    //版本更新广播
    private static class UpgradeBroadcastReceiver extends BroadcastReceiver {
        private DownloadManager dManager;
        private long reference;

        public UpgradeBroadcastReceiver(DownloadManager dManager, long reference) {
            this.dManager = dManager;
            this.reference = reference;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long myDownloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference == myDownloadID) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    downloadFileUri = dManager.getUriForDownloadedFile(reference);
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                }
            }
        }
    }

    /**
     * 给javaScript调用的类以及方法
     */
    public class JsToJava {

        //自己做直播js调用的方法
        @JavascriptInterface
        public void openCamera(String s) {
            Intent in = new Intent(WebActivity.this, PushParamsActivity.class);
            in.putExtra("pushurl", lri.getPushUrl());
            in.putExtra("eventurl", lri.getEventUrl());
            in.putExtra("openid", lri.getOpenId());
            in.putExtra("memberlevelid", lri.getMemberlevelid());
            in.putExtra("isYaoyue", false);
            in.putParcelableArrayListExtra("products_list", pList);
            shareInfo.setLiveId(s);
            in.putExtra("shareinfo", shareInfo);
            startActivity(in);
        }

        //别人邀约自己做直播js调用的方法
        @JavascriptInterface
        public void openCamera(String liveId, String json) {
            //清空自己的直播间的商品
            if (pList.size() != 0) pList.clear();
            //邀约直播的参数
            Intent in = new Intent(WebActivity.this, PushParamsActivity.class);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String pushUrl = jsonObject.getString("rtmp");
                String memberLevel = jsonObject.getString("memberlevel");
                String openId = jsonObject.getString("deleid");
                String goods = jsonObject.getString("goods");
                JSONArray object_goods = new JSONArray(goods);
                for (int i = 0; i < object_goods.length(); i++) {
                    JSONObject object_good = object_goods.getJSONObject(i);
                    products = new Products();
                    products.setProducts_id(object_good.getString("id"));
                    products.setProducts_name(object_good.getString("name"));
                    products.setProducts_price(object_good.getString("price"));
                    String baseUrl = object_good.getString("img").startsWith("//") ? "http:" : mBase;
                    products.setProducts_icon(baseUrl + object_good.getString("img"));
                    pList.add(products);
                }
                in.putExtra("pushurl", pushUrl);
                in.putExtra("openid", openId);
                in.putExtra("memberlevelid", memberLevel);
                in.putExtra("eventurl", lri.getEventUrl());
                in.putParcelableArrayListExtra("products_list", pList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            shareInfo.setLiveId(liveId);
            in.putExtra("shareinfo", shareInfo);
            startActivity(in);
        }

        //接收js回传的信息包含（商品,推流,会员相关）
        @JavascriptInterface
        public void setProfile(String json) {//base属性 + img属性  拼接去取图片  goods是JSONobject
            if (json != null) {
                try {
                    pList = new ArrayList<>();
                    //解析json
                    JSONObject object = new JSONObject(json);
                    lri = new LiveRoomInfo();
                    lri.setuId(object.getString("uid"));
                    lri.setAcId(object.getString("acid"));
                    lri.setNickName(object.getString("nickname"));
                    lri.setOpenId(object.getString("openid"));
                    lri.setEventUrl(object.getString("eventurl"));
                    lri.setPushUrl(object.getString("pushurl"));
                    lri.setRoomImgUrl(object.getString("roomimg"));
                    lri.setMemberlevelid(String.valueOf(object.getInt("memberlevelid")));
                    String goods = object.getString("goods");
                    JSONArray object_goods = new JSONArray(goods);
                    for (int i = 0; i < object_goods.length(); i++) {
                        JSONObject object_good = object_goods.getJSONObject(i);
                        products = new Products();
                        products.setProducts_id(object_good.getString("id"));
                        products.setProducts_name(object_good.getString("name"));
                        products.setProducts_price(object_good.getString("price"));
                        mBase = object.getString("base");
                        String mBaseUrl = object_good.getString("img").startsWith("//") ? "http:" : object.getString("base");
                        products.setProducts_icon(mBaseUrl + object_good.getString("img"));
                        pList.add(products);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //js端通过unionId判断用户相关
        @JavascriptInterface
        public String getToken() {
            String unionId = getSharedPreferences("unionId", WebActivity.MODE_PRIVATE).getString("unionId", "");
            return unionId;
        }

        //接收js回传的分享信息
        @JavascriptInterface
        public void setShare(String json) {
            try {
                final JSONObject object = new JSONObject(json);
                shareInfo = new ShareInfo();
                shareInfo.setTitle(object.getString("title"));
                shareInfo.setDesc(object.getString("desc"));
                shareInfo.setLink(object.getString("link"));
                shareInfo.setImgUrl(object.getString("imgUrl"));
                sList.add(shareInfo);
                //获取分享缩略图
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mBitmap = HttpUtils.getBitmapByUrl(object.getString("imgUrl"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //app版本号
        @JavascriptInterface
        public int getVersion() {
            return versionCode;
        }
    }
}
