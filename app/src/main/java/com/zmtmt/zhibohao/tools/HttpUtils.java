package com.zmtmt.zhibohao.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/1.
 */

public class HttpUtils {
    //网络请求返回json  GET
    @Nullable //表示定义的字段可以为空
    public static void get(String url,NetWorkStatus netWorkStatus) {
        BufferedReader bufferedReader = null;
        HttpURLConnection connection=null;
        try {
            URL urls = new URL(url);
            connection = (HttpURLConnection) urls.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            if (connection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                String str;
                StringBuilder sb = new StringBuilder();
                while ((str = bufferedReader.readLine()) != null) {
                    sb.append(str);
                }
                netWorkStatus.onSuccessful(sb.toString());
            }
        } catch (Exception e) {
            netWorkStatus.onFailed(e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
    }

    //网络请求 POST
    public static void post(String url, Map<String, String> params,NetWorkStatus netWorkStatus) {
        BufferedReader bufferedReader = null;
        HttpURLConnection conn = null;
        try {
            URL urls = new URL(url);
            conn = (HttpURLConnection) urls.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            //设置允许向服务器获取数据
            conn.setDoInput(true);
            if (params != null) {
                //设置允许向服务器提交数据
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                //请求的数据
                String request = "";
                for (String key : params.keySet()) {
                    request += key + "=" + params.get(key) + "&";
                }
                //多了一个拼接符
                String out = request.substring(0, request.length() - 1);
                os.write(out.getBytes());
                os.flush();
                os.close();
            }
            if (conn.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String response;
                while ((response = bufferedReader.readLine()) != null) {
                    sb.append(response);
                }
                netWorkStatus.onSuccessful(response);
            }
        } catch (MalformedURLException e) {
            netWorkStatus.onFailed(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            netWorkStatus.onFailed(e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * @param url 请求的url地址
     * @return 返回的流转换为Bitmap对象
     */
    public static Bitmap getBitmapByUrl(String url) {
        Bitmap bitmap = null;
        try {
            URL urls = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                conn.disconnect();
                is.close();
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface NetWorkStatus{
        void onSuccessful(String response);
        void onFailed(String error);
    }
}
