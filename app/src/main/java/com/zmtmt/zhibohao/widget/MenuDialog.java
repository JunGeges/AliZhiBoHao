package com.zmtmt.zhibohao.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.zmtmt.zhibohao.R;


public class MenuDialog extends LinearLayout {
    public static final String DEFAULT_RESOLUTION = "hight";

    public static final String ORIENTATION_LANDSCAPE = "landscape";
    public static final String DEFAULT_ORIENTATION = ORIENTATION_LANDSCAPE;
    public static final String ORIENTATION_PORTRAIT = "portrait";


    private EditText mStreamEditText;
    private RadioGroup mOrientationGroup;
    private RadioGroup mResolutionGroup;

    public MenuDialog(Context context) {
        super(context);
        init(context);
    }

    public MenuDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        Resources r = context.getResources();
        int pxPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        setPadding(pxPadding, pxPadding, pxPadding, pxPadding);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.menu_dialog, this);
        mStreamEditText = (EditText) view.findViewById(R.id.stream_url);
        mResolutionGroup = (RadioGroup)view.findViewById(R.id.resolution);
        mResolutionGroup.check(R.id.resolution_hight);

        mOrientationGroup = (RadioGroup)view.findViewById(R.id.orientation);
        mOrientationGroup.check(R.id.orientation_landscape);
    }


    public String getStreamUrl() {
        return mStreamEditText.getText().toString();
    }

    public String getResolution() {
        String result = DEFAULT_RESOLUTION;
        int rid = mResolutionGroup.getCheckedRadioButtonId();
        switch (rid) {
            case R.id.resolution_xhight: {
                result = "xhight";
            }
            break;
            case R.id.resolution_standard: {
                result = "standard";
            }
            break;
            case R.id.resolution_fluent: {
                result = "fluent";
            }
            break;
        }
        return result;
    }

    public void setStreamUrl(String stream) {
        mStreamEditText.setText(stream);
    }

    public void setResolution(String resolution) {
        int rid = R.id.resolution_hight;
        switch (resolution) {
            case "xhight": {
                rid = R.id.resolution_xhight;
            }
            break;
            case "standard": {
                rid = R.id.resolution_standard;
            }
            break;
            case "fluent": {
                rid = R.id.resolution_fluent;
            }
            break;
        }

        mResolutionGroup.check(rid);
    }

    public void setStreamUrlErrorInfo() {
        mStreamEditText.setError("请输入推流地址");
    }

    public void setOrientationVal(String orientationVal) {
        int rid = R.id.orientation_landscape;
        switch (orientationVal) {
            case "portrait": {
                rid = R.id.orientation_portrait;
            }
            break;
            case "auto": {
                rid = R.id.orientation_auto;
            }
            break;
        }
        mOrientationGroup.check(rid);
    }

    /**
     * 横竖屏切换设置
     * @return 返回设置的参数
     */
    public String getOrientationVal() {
        //默认值
        String result = DEFAULT_ORIENTATION;
        //获取单选组的按钮的ID
        int rid = mOrientationGroup.getCheckedRadioButtonId();
        switch (rid) {
            case R.id.orientation_portrait: {
                result = "portrait";
            }
            break;
            case R.id.orientation_landscape: {
                result = "landscape";
            }
            break;
            case R.id.orientation_auto: {
                result = "auto";
            }
            break;
        }
        return result;
    }
}
