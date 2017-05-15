package com.zmtmt.zhibohao.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmtmt.zhibohao.R;

/**
 * Created by Administrator on 2017/5/2.
 */

public class CountTimeView extends LinearLayout implements Runnable {
    private static final int UI_EVENT_START_TIME = 1;
    private static final int UI_EVENT_STOP_TIME = 2;
    private static final int UI_EVENT_START = 3;
    private Thread mThread;
    private TextView mtv_time_s, mtv_time_m, mtv_time_h;
    private int time_s = 0;
    private int time_m = 0;
    private int time_h = 0;
    private View mView;
    private boolean mFlag;
    private int count = 0;
    private ImageView iv_time_switch;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UI_EVENT_START_TIME:
                    countTime();
                    count++;
                    iv_time_switch.setVisibility(count % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
                    break;

                case UI_EVENT_STOP_TIME:
                    mFlag = false;
                    break;

                case UI_EVENT_START:
                    if (!mThread.isAlive()) {
                        //开始计时器或者是重启计时器，设置标记为true
                        mFlag = true;
                        //判断是否是第一次启动，如果是不是第一次启动，那么状态就是Thread.State.TERMINATED
                        //不是的话，就需要重新的初始化，因为之前的已经结束了。
                        //并且要判断这个mCount 是否为-1，如果是的话，说明上一次的计时已经完成了，那么要重新设置。
                        if (mThread.getState() == Thread.State.TERMINATED) {
                            mThread = new Thread(CountTimeView.this);
                            mThread.start();
                        } else {
                            mThread.start();
                        }
                    } else {
                        mFlag = false;//暂停计时器，设置标记为false
                    }
                    break;
            }
        }
    };

    public CountTimeView(Context context) {
        super(context);
        init(context);
    }

    public CountTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        //时间子线程的初始化
        mThread = new Thread(this);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mView = layoutInflater.inflate(R.layout.count_time_view, this, true);
    }

    /**
     * 当布局加载完成时调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mtv_time_s = (TextView) mView.findViewById(R.id.tv_time_second);
        mtv_time_m = (TextView) mView.findViewById(R.id.tv_time_minute);
        mtv_time_h = (TextView) mView.findViewById(R.id.tv_time_hour);
        iv_time_switch = (ImageView) mView.findViewById(R.id.iv_time_switch);
    }

    public void startCountTime() {
        mHandler.sendEmptyMessage(UI_EVENT_START);
    }

    public void stopCountTime() {
        mFlag = false;
    }

    //计时方法
    private void countTime() {
        time_s++;
        if (time_s <= 9) {
            mtv_time_s.setText("0" + time_s);
        } else {
            mtv_time_s.setText(time_s + "");
        }
        if (time_s == 59) {
            mtv_time_s.setText("00");
            time_m++;
            time_s = 0;
            if (time_m <= 9) {
                mtv_time_m.setText("0" + time_m);
            } else {
                mtv_time_m.setText(time_m + "");
            }
        }
        if (time_m == 59) {
            mtv_time_m.setText("00");
            time_h++;
            time_m = 0;
            if (time_h <= 9) {
                mtv_time_h.setText("0" + time_h);
            } else {
                mtv_time_h.setText(time_h);
            }
        }
    }

    /**
     * 获取总时间
     */
    public long getTotalTime() {
        long liveTime = 0;
        //获取时间
        String h = mtv_time_h.getText().toString().trim();
        String mm = mtv_time_m.getText().toString().trim();
        String ss = mtv_time_s.getText().toString().trim();
        if (h.startsWith("0")) {
            liveTime += Integer.parseInt(h.substring(1)) * 3600;
        } else {
            liveTime += Integer.parseInt(h) * 3600;
        }
        if (mm.startsWith("0")) {
            liveTime += Integer.parseInt(mm.substring(1)) * 60;
        } else {
            liveTime += Integer.parseInt(mm) * 60;
        }
        if (ss.startsWith("0")) {
            liveTime += Integer.parseInt(ss.substring(1));
        } else {
            liveTime += Integer.parseInt(ss);
        }
        return liveTime;
    }

    @Override
    public void run() {
        while (mFlag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = Message.obtain();
            message.what = UI_EVENT_START_TIME;
            mHandler.sendMessage(message);
        }
    }
}
