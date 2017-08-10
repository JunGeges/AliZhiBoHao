package com.zmtmt.zhibohao.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zmtmt.zhibohao.R;

public class SpeedTestActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView tv_0,tv_1,tv_2,tv_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        initViews();
        initWeb();
    }

    private void initViews() {
        mWebView = (WebView) findViewById(R.id.speed_test_wv);
        tv_0=(TextView)findViewById(R.id.tv_0);
        tv_1=(TextView)findViewById(R.id.tv_1);
        tv_2=(TextView)findViewById(R.id.tv_2);//0e94af
        tv_3=(TextView)findViewById(R.id.tv_3);
        buildSpannableString(tv_0,Color.RED,new Integer[]{5,15,18,22});
        buildSpannableString(tv_1,Color.parseColor("#0E94AF"),new Integer[]{5,17,20,26});
        buildSpannableString(tv_2,Color.parseColor("#0E94AF"),new Integer[]{5,15,18,24});
        buildSpannableString(tv_3,Color.parseColor("#0E94AF"),new Integer[]{5,13,16,22});
    }

    public void buildSpannableString(TextView tv,int color,Integer... ints){
        SpannableString spannableString=new SpannableString(tv.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(color),ints[0],ints[1], Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color),ints[2],ints[3], Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tv.setText(spannableString);
    }

    private void initWeb() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.loadUrl("file:///android_asset/speedtest.html");
        mWebView.setWebViewClient(new WebViewClient());
    }
}
