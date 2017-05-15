package com.zmtmt.zhibohao.entity;

/**
 * Created by Administrator on 2016/7/28.
 */
public class WxAccessToken {
    /**
     * 接口调用凭证
     */
    private String access_token;
    /**
     * 接口调用凭证超时时间，单位（秒）
     */
    private String expires_in;
    /**
     *用户刷新access_token
     */
    private String refresh_token;
    /**
     * 用户授权唯一标识
     */
    private String openId;
    /**
     * 用户授权的作用域，使用逗号分隔
     */
    private String scope;
    /**
     * 用户授权作用域,使用逗号分隔
     */
    private String unionId;
    /**
     * 错误码
     */
    private String errcode;
    /**
     * 错误信息
     */
    private String errmsg;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "WxAccessToken{" +
                "access_token='" + access_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", openId='" + openId + '\'' +
                ", scope='" + scope + '\'' +
                ", unionId='" + unionId + '\'' +
                ", errcode='" + errcode + '\'' +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
