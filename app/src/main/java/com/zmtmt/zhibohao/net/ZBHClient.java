package com.zmtmt.zhibohao.net;

import android.support.constraint.BuildConfig;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 初始化请求服务
 */

public class ZBHClient {
    private static ZBHClient mZBHClient;

    private OkHttpClient.Builder mBuilder;

    private ZBHClient(){
        initSetting();
    }

    //初始化ZBHClient
    public static ZBHClient getInstance(){
        if(mZBHClient==null){
            synchronized (ZBHClient.class){
                if(mZBHClient==null){
                    mZBHClient = new ZBHClient();
                }
            }
        }
        return mZBHClient;
    }

    /***
     * 创建相应的接口服务
     */
    public <T> T create (Class<T> service,String baseUrl){
        checkNotNull(service,"service is null");
        checkNotNull(baseUrl,"baseUrl is null");

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持RxJava
                .build()
                .create(service);
    }

    private <T> T checkNotNull (T object,String message){
        if(object==null){
            throw new NullPointerException(message);
        }
        return object;
    }


    private void initSetting() {
        //初始化OkHttp
        mBuilder = new OkHttpClient.Builder()
                .connectTimeout(9, TimeUnit.SECONDS)//设置连接超时为9秒
                .readTimeout(10,TimeUnit.SECONDS);//设置请求超时为10秒

        if(BuildConfig.DEBUG){
            //添加日志拦截器
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mBuilder.addInterceptor(interceptor);
        }

    }
}
