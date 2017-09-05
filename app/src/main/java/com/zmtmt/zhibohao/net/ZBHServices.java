package com.zmtmt.zhibohao.net;

import com.zmtmt.zhibohao.entity.VersionBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 请求接口不规范不好重构  强行用了一波RxJaVA+Retrofit
 */

public interface ZBHServices {
    //http://app.zmtmt.com/down/zhibohao.json
    //版本请求信息
    @GET("down/zhibohao.json")
    Observable<VersionBean> getVersionInfo ();

    @POST("do=applogin")
    @FormUrlEncoded
    Observable<ResponseBody> requestChangeLive(@FieldMap Map<String,String> map);
}
