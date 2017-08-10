package com.zmtmt.zhibohao.wxapi;

/**
 * Created by Administrator on 2016/7/28.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.zmtmt.zhibohao.app.MyApplication;
import com.zmtmt.zhibohao.R;
import com.zmtmt.zhibohao.activity.WebActivity;
import com.zmtmt.zhibohao.entity.User;
import com.zmtmt.zhibohao.entity.WxAccessToken;
import com.zmtmt.zhibohao.tools.HttpUtils;
import com.zmtmt.zhibohao.tools.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/21.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private static final int WXSHARE = 2;
    private static final int WXLOGIN = 1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_activity_layout);
        MyApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onStart() {
        finish();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 微信授权后回调
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        int result = 0;
        //type=1  登录回调   type=2 分享回调
        int type = baseResp.getType();
        switch (baseResp.errCode) {
            //用户授权成功
            case BaseResp.ErrCode.ERR_OK:
                if (type == WXLOGIN) {
                    result = R.string.errcode_success;
                    if (baseResp instanceof SendAuth.Resp) {
                        SendAuth.Resp aures = (SendAuth.Resp) baseResp;
                        //获取code
                        String code = aures.code;
                        //请求获取UserInfo
                        getAccessToken(code);
                        Intent in = new Intent(WXEntryActivity.this, WebActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(in);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }

                if (type == WXSHARE) {
                    CommonUtils.showToast(this, "分享成功");
                }

                break;

            //用户取消授权
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                if (type == WXLOGIN) {
                    finish();
                }
                if (type == WXSHARE) {
                    CommonUtils.showToast(this, "分享已取消");
                }
                break;

            //用户拒绝授权
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;

            default:
                result = R.string.errcode_unknown;
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MyApplication.api.handleIntent(intent, this);
    }

    /**
     * 通过code获取AccessToken
     *
     * @param code 微信返回码
     */
    public void getAccessToken(String code) {
        //接口
        final String url = "" + MyApplication.URL_ACCESS_TOKEN + "appid=" + MyApplication.APP_ID + "&secret=" + MyApplication.APPSECRET + "&code=" + code + "&grant_type=authorization_code";
        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpUtils.get(url, new HttpUtils.NetWorkStatus() {
                    @Override
                    public void onSuccessful(String response) {
                        try {
                            WxAccessToken tokens = new WxAccessToken();
                            JSONObject object = new JSONObject(response);
                            tokens.setAccess_token(object.getString("access_token"));
                            tokens.setExpires_in(object.getString("expires_in"));
                            tokens.setRefresh_token(object.getString("refresh_token"));
                            tokens.setOpenId(object.getString("openid"));
                            tokens.setScope(object.getString("scope"));
                            tokens.setUnionId(object.getString("unionid"));
                            //获取用户信息
                            User userInfo = getUserInfo(tokens);
                            //保存用户logo的url地址 和昵称
                            CommonUtils.saveToSp(WXEntryActivity.this, "WXUserParams", new String[]{"WXLogoUrl", "WXNickName"}, new String[]{userInfo.getHeadImgUrl(), userInfo.getNickName()});
                            Log.i(TAG, "onSuccessful: " + tokens);
                            //存unionid
                            CommonUtils.saveToSp(WXEntryActivity.this, "unionId", new String[]{"unionId"}, userInfo.getUnionid());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });
            }
        }.start();
    }

    /**
     * 获取用户信息
     *
     * @param token
     */
    public User getUserInfo(WxAccessToken token) {
        String url = "" + MyApplication.URL_USERINFO + "access_token=" + token.getAccess_token() + "&openid=" + token.getOpenId();
        HttpUtils.get(url, new HttpUtils.NetWorkStatus() {
            @Override
            public void onSuccessful(String response) {
                if (response != null) {
                    try {
                        user = new User();
                        JSONObject object = new JSONObject(response);
                        user.setOpenId(object.getString("openid"));
                        user.setNickName(object.getString("nickname"));
                        user.setSex(object.getInt("sex"));
                        user.setProvince(object.getString("province"));
                        user.setCity(object.getString("city"));
                        user.setCountry(object.getString("country"));
                        user.setHeadImgUrl(object.getString("headimgurl"));
                        JSONArray array = object.getJSONArray("privilege");
                        String[] privilege = {};
                        for (int i = 0; i < array.length(); i++) {
                            privilege[0] = array.getJSONObject(0).toString();
                            privilege[1] = array.getJSONObject(1).toString();
                        }
                        user.setPrivilege(privilege);
                        user.setUnionid(object.getString("unionid"));
                        Log.i(TAG, "UserInfo: " + user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(String error) {

            }
        });
        return user;
    }
}
